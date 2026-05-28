# Guía P7 — CD con GitHub Actions + DockerHub + Kubernetes

## ¿Qué hace este workflow?

El fichero `.github/workflows/cd.yml` define un pipeline de despliegue continuo (CI/CD) que se ejecuta automáticamente cada vez que se hace un push a `main`. El pipeline:

1. Compila el proyecto Spring Boot y genera el JAR
2. Construye la imagen Docker y la sube a DockerHub
3. Se conecta al clúster Kubernetes local via self-hosted runner
4. Actualiza el deployment con la nueva imagen automáticamente
5. Espera a que el rollout termine y verifica el estado

---

## Requisitos previos (antes del workflow)

Antes de que el workflow funcione hay que hacer tres cosas manualmente:

### 1. Crear el Dockerfile

En la raíz del proyecto (al mismo nivel que `pom.xml`):

```dockerfile
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

- `eclipse-temurin:17-jre` — imagen base con Java 17 (solo JRE, más ligera que JDK)
- `WORKDIR /app` — directorio de trabajo dentro del contenedor
- `COPY target/*.jar app.jar` — copia el JAR generado por Maven
- `EXPOSE 8080` — documenta el puerto (Spring Boot por defecto)
- `ENTRYPOINT` — comando que arranca la app

### 2. Crear el deployment.yml de Kubernetes

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: hospital-app
spec:
  replicas: 2
  selector:
    matchLabels:
      app: hospital-app
  template:
    metadata:
      labels:
        app: hospital-app
    spec:
      containers:
      - name: hospital-app
        image: josejavigb/p7-hospital-cicd:latest
        imagePullPolicy: Always        # Siempre descarga desde DockerHub
        ports:
        - containerPort: 8080
---
apiVersion: v1
kind: Service
metadata:
  name: hospital-app-service
spec:
  type: NodePort
  selector:
    app: hospital-app
  ports:
  - port: 8080
    targetPort: 8080
    nodePort: 30080
```

**Reglas críticas:**
- `imagePullPolicy: Always` — necesario para que K8s descargue la imagen de DockerHub. Con `Never` solo busca en local y falla.
- El label `app: hospital-app` debe coincidir en 3 sitios: `matchLabels`, `template.labels` y `selector` del Service.
- `nodePort: 30080` — puerto accesible desde el host en `http://localhost:30080`

### 3. Deploy inicial manual

```powershell
# Compilar y generar el JAR
./mvnw package -DskipTests --no-transfer-progress

# Construir imagen local (solo la primera vez)
docker build -t hospital-app:latest .

# Deploy inicial en K8s
kubectl apply -f deployment.yml

# Verificar que los pods están Running
kubectl get pods
```

---

## Secrets y Variables necesarios

Configurar en **Settings → Secrets and variables → Actions** del repo:

| Nombre | Tipo | Valor |
|---|---|---|
| `DOCKERHUB_USERNAME` | 🔒 Secret | Tu usuario de DockerHub |
| `DOCKERHUB_TOKEN` | 🔒 Secret | Token de acceso (no la contraseña) |
| `REGISTRY` | 👁️ Variable | `docker.io` |
| `DEPLOYMENT_NAME` | 👁️ Variable | `hospital-app` |
| `NAMESPACE` | 👁️ Variable | `default` |

---

## Self-hosted Runner

El job de deploy necesita acceder al clúster Kubernetes local — los runners de GitHub (cloud) no pueden acceder a tu red local. Por eso se usa un runner self-hosted.

### Configuración (una sola vez)

1. Ve a **Settings → Actions → Runners → New self-hosted runner**
2. Selecciona Windows x64
3. Ejecuta los comandos que te da GitHub en PowerShell:

```powershell
# Crear carpeta
mkdir actions-runner; cd actions-runner

# Descargar runner
Invoke-WebRequest -Uri https://github.com/actions/runner/releases/download/v2.334.0/actions-runner-win-x64-2.334.0.zip -OutFile actions-runner-win-x64-2.334.0.zip
Add-Type -AssemblyName System.IO.Compression.FileSystem
[System.IO.Compression.ZipFile]::ExtractToDirectory("$PWD/actions-runner-win-x64-2.334.0.zip", "$PWD")

# Configurar con el token que da GitHub
./config.cmd --url https://github.com/USUARIO/REPO --token TOKEN_DE_GITHUB
```

### Arrancar antes del examen

```powershell
cd ruta\a\actions-runner
./run.cmd
```

Deja esta terminal abierta. El runner aparecerá como **Idle** en Settings → Actions → Runners.

**Estados del Runner:**
| Estado | Significado |
|---|---|
| Idle ✅ | Activo y esperando jobs |
| Active ✅ | Ejecutando un job ahora |
| Offline ❌ | No está corriendo — ejecutar ./run.cmd |

---

## Estructura del workflow

### Variable de entorno global (`env:`)

```yaml
env:
  IMAGE_NAME: ${{ secrets.DOCKERHUB_USERNAME }}/p7-hospital-cicd
```

Se define a nivel de workflow (fuera de `jobs:`) para ser accesible en todos los jobs. El nombre de la imagen **debe estar en minúsculas** — DockerHub no acepta mayúsculas.

---

## Jobs detallados

### Job 1: build-and-publish (`ubuntu-latest`)

Corre en los servidores de GitHub. Se encarga de compilar, construir la imagen y subirla a DockerHub.

#### Set up Java + Build JAR

```yaml
- name: Set up Java 17
  uses: actions/setup-java@v4
  with:
    java-version: '17'
    distribution: 'temurin'
    cache: maven

- name: Give permissions to mvnw
  run: chmod +x ./mvnw

- name: Build JAR
  run: ./mvnw package -DskipTests --no-transfer-progress
```

`-DskipTests` — omite los tests para que el build sea más rápido. El JAR se genera en `target/`.

#### Las 5 Actions de Docker (en orden)

```yaml
- name: Set up QEMU
  uses: docker/setup-qemu-action@v3

- name: Set up Docker Buildx
  uses: docker/setup-buildx-action@v3

- name: Login a DockerHub
  if: github.event_name != 'pull_request'
  uses: docker/login-action@v3
  with:
    registry: ${{ vars.REGISTRY }}
    username: ${{ secrets.DOCKERHUB_USERNAME }}
    password: ${{ secrets.DOCKERHUB_TOKEN }}

- name: Extract Docker metadata
  id: meta
  uses: docker/metadata-action@v5
  with:
    images: ${{ vars.REGISTRY }}/${{ env.IMAGE_NAME }}
    tags: |
      type=ref,event=branch
      type=ref,event=tag
      type=sha,format=short
      type=raw,value=latest,enable={{is_default_branch}}

- name: Build and push Docker image
  uses: docker/build-push-action@v5
  with:
    context: .
    push: ${{ github.event_name != 'pull_request' }}
    tags: ${{ steps.meta.outputs.tags }}
    labels: ${{ steps.meta.outputs.labels }}
    cache-from: type=gha
    cache-to: type=gha,mode=max
    platforms: linux/amd64
```

| Action | Para qué |
|---|---|
| `setup-qemu-action` | Emulación multi-arquitectura |
| `setup-buildx-action` | Activa Docker Buildx (builder avanzado) |
| `login-action` | Login a DockerHub con secrets |
| `metadata-action` | Genera tags automáticos (latest, sha, rama) |
| `build-push-action` | Build + Push en un solo step |

**Tags generados automáticamente:**
- `latest` — cuando se hace push a main
- `main` — nombre de la rama
- `sha-abc1234` — SHA corto del commit (7 caracteres)

**`id: meta` es obligatorio** — sin él no puedes usar `steps.meta.outputs.tags` en el build-push.

**Login solo en push, no en PR** — `if: github.event_name != 'pull_request'` evita exponer secrets en PRs de forks.

---

### Job 2: deploy-to-k8s (`self-hosted`)

Corre en tu máquina local. Tiene acceso al clúster Kubernetes.

```yaml
deploy-to-k8s:
  needs: build-and-publish    # No arranca hasta que la imagen esté en DockerHub
  runs-on: self-hosted        # Tu máquina — tiene kubectl y acceso al clúster
```

#### Update deployment image

```yaml
- name: Update deployment image
  run: |
    kubectl set image deployment/${{ vars.DEPLOYMENT_NAME }} `
      ${{ vars.DEPLOYMENT_NAME }}=${{ vars.REGISTRY }}/${{ env.IMAGE_NAME }}:sha-$($env:GITHUB_SHA.Substring(0,7)) `
      --namespace=${{ vars.NAMESPACE }}
```

Actualiza la imagen del deployment con el tag SHA del commit. Kubernetes detecta el cambio y recrear los pods automáticamente.

**Sintaxis de kubectl set image:**
```
kubectl set image deployment/NOMBRE_DEPLOYMENT NOMBRE_CONTENEDOR=IMAGEN:TAG
```
El `NOMBRE_CONTENEDOR` debe coincidir exactamente con el `name:` del contenedor en el `deployment.yml`.

**SHA corto en PowerShell (self-hosted Windows):**
```powershell
$env:GITHUB_SHA.Substring(0,7)   # → primeros 7 caracteres del SHA
```

#### Wait for rollout

```yaml
- name: Wait for deployment rollout
  run: |
    kubectl rollout status deployment/${{ vars.DEPLOYMENT_NAME }} `
      --namespace=${{ vars.NAMESPACE }} `
      --timeout=60s
```

Espera hasta que todos los pods estén Running. Si tarda más de 60s falla el step. Aumentar a `120s` si la imagen tarda en descargarse.

#### Verify deployment

```yaml
- name: Verify deployment
  run: |
    kubectl get deployment ${{ vars.DEPLOYMENT_NAME }} --namespace=${{ vars.NAMESPACE }}
    kubectl get pods --namespace=${{ vars.NAMESPACE }}
```

Muestra el estado final del deployment y los pods en los logs del workflow.

---

## Errores comunes y soluciones

| Error | Causa | Solución |
|---|---|---|
| `Permission denied ./mvnw` | Falta chmod | Añadir `chmod +x ./mvnw` antes del build |
| `lstat /target: no such file` | No se generó el JAR | Añadir step de `mvn package` antes del Docker build |
| `repository name must be lowercase` | Nombre de repo con mayúsculas | Fijar `IMAGE_NAME` en minúsculas en el `env:` |
| `ErrImageNeverPull` | `imagePullPolicy: Never` en deployment.yml | Cambiar a `imagePullPolicy: Always` |
| `ImagePullBackOff` | Imagen incorrecta en deployment.yml | Apuntar a la imagen de DockerHub correcta |
| `timed out waiting` | Rollout tarda más del timeout | Aumentar `--timeout=60s` a `--timeout=120s` |
| Runner en Offline | `./run.cmd` no está ejecutándose | Abrir PowerShell y ejecutar `./run.cmd` en la carpeta del runner |

---

## Flujo completo

```
git push → GitHub detecta el push
         → Arranca runner ubuntu-latest (build-and-publish)
         → Checkout del código
         → Instala Java 17
         → chmod +x ./mvnw
         → mvn package → genera el JAR
         → Set up QEMU + Buildx
         → Login a DockerHub
         → Genera tags (latest, main, sha-abc1234)
         → docker build + push → imagen en DockerHub ✅
         → Arranca runner self-hosted (deploy-to-k8s)
         → Checkout
         → kubectl set image → K8s actualiza el deployment
         → kubectl rollout status → espera pods Running
         → kubectl get pods → verificación final ✅
         → Pods recreados con la nueva imagen automáticamente 🎉
```

---

## Comandos kubectl útiles

```powershell
# Ver estado de los pods
kubectl get pods

# Ver pods en tiempo real
kubectl get pods -w

# Ver todo
kubectl get all

# Aplicar manifiesto
kubectl apply -f deployment.yml

# Actualizar imagen manualmente
kubectl set image deployment/hospital-app hospital-app=josejavigb/p7-hospital-cicd:latest

# Esperar rollout
kubectl rollout status deployment/hospital-app

# Revertir al deploy anterior
kubectl rollout undo deployment/hospital-app

# Ver logs de un pod
kubectl logs NOMBRE_POD

# Describir pod (ver errores)
kubectl describe pod NOMBRE_POD
```
