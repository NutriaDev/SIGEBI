<div align="center">

<h1>SIGEBI</h1>
<h3>Sistema de Interfaz de Gestión de Equipos Biomédicos</h3>

<p>
SIGEBI es un sistema orientado a la <strong>gestión, control y auditoría de equipos biomédicos,</strong> diseñado para instituciones de salud.
Permite llevar trazabilidad completa sobre usuarios, equipos, inventarios, mantenimientos, reportes y auditorías, cumpliendo buenas prácticas de seguridad, integridad de datos y escalabilidad.

<br>

El sistema está pensado como una solución empresarial, con control de accesos por roles y registro detallado de eventos para soportar procesos de auditoría y normativas de seguridad.
</p>

</div>

---

<details>
<summary><h2>🏗️ Arquitectura</h2></summary>

<p>
SIGEBI está construido bajo una arquitectura de <strong>microservicios en capas</strong>,
donde cada servicio es independiente y cumple una responsabilidad específica.
</p>

</details>

---


<details>
<summary><h2>🚀 Diagrama de Despliegue</h2></summary>

<p>
Describe cómo se distribuyen los microservicios, bases de datos y componentes
de infraestructura en los distintos entornos.
</p>

<!-- Aquí va la imagen del diagrama de despliegue -->

<img width="726" height="1045" alt="image" src="https://github.com/user-attachments/assets/35e72529-8eef-4fba-8f1f-40e73586c4b9" />

</details>

---

<details>
<summary><h2>🛠️ ¿Cómo preparar tu entorno?</h2></summary>

<ul align="left">
  <li>Java 17</li>
  <li>Maven</li>
  <li>PostgreSQL</li>
  <li>Docker (opcional)</li>
  <li>IntelliJ IDEA (recomendado)</li>
</ul>

## 🛠️ Crear un nuevo Microservicio en SIGEBI  
### Guía para desarrolladores

---

### 📥 Clonar el repositorio principal y acceder al proyecto

```bash
git clone [URL_DEL_REPO]
cd SigebiMicroservice
```

### ⚙️ Ejecutar el build general del proyecto

```bash
mvn clean install
```
<p>✅ En consola debe aparecer algo similar a:</p>

```bash
[INFO] Building config 0.0.1-SNAPSHOT
[INFO] Building eureka 0.0.1-SNAPSHOT
[INFO] Building gateway 0.0.1-SNAPSHOT
[INFO] Building users 0.0.1-SNAPSHOT
[INFO] Building equipment 0.0.1-SNAPSHOT
[INFO] BUILD SUCCESS
```
### ❌ Si alguno de los módulos falla, revisar los logs ubicados en:

```bash
SigebiMicroservice/<modulo>/target/surefire-reports/
```
<p>y solucionar el inconveniente antes de continuar (puedes apoyarte en IA).</p>


### 🌱 Crear una nueva rama desde develop

```bash
git checkout develop
git pull origin develop
git flow feature start new-branch

```
<p>NOTA: Es importante tener git flow iniciado</p>


### 🧩 Crear un nuevo Microservicio


<strong>1.</strong> Ingresa a 👉 https://start.spring.io/
<strong>2.</strong> Configura el proyecto con los siguientes valores:

  ```bash
Project:        Maven
Language:       Java
Spring Boot:    3.5.2
Java:           17
Packaging:      Jar
Configuration:  YAML
Group:          sigebi
Artifact / Name: equipment (o el MS correspondiente)
Description:    descripcion del microservicio
 ```
<strong>3.</strong> 📦 Dependencias obligatorias del microservicio

<ul align="left">
  <li>Spring Web</li>
  <li>Spring Data JPA</li>
  <li>Spring Boot Actuator</li>
  <li>Eureka Discovery Client</li>
  <li>PostgreSQL Driver</li>
  <li>Validation (Jakarta Validation)</li>
  <li>Spring Boot Starter Test</li>
</ul>

<strong>4.</strong> Descarga el proyecto y ubícalo dentro del directorio:
```bash
SigebiMicroservice/
```

</details>

---

<details>
<summary><h2>🧩 Microservicios</h2></summary>

<ul align="left">
  <li><strong><a href="SigebiMicroservice/users/README.md">Users</a></strong> – Gestión de usuarios y roles</li>
  <li><strong><a href="SigebiMicroservice/auth/README.md">Auth / JWT</a></strong> – Autenticación y seguridad</li>
  <li><strong><a href="SigebiMicroservice/equipment/README.md">Equipment</a></strong> – Gestión de equipos médicos</li>
  <li><strong><a href="SigebiMicroservice/cms/README.md">CMS</a></strong> – Gestión de imágenes de equipos</li>
  <li><strong>Inventory</strong> – Control de inventarios</li>
  <li><strong><a href="SigebiMicroservice/maintenance/README.md">Maintenance</a></strong> – Mantenimientos y programación</li>
  <li><strong><a href="SigebiMicroservice/reportsandaudit/README.md">Reports & Audit</a></strong> – Reportes y auditoría</li>
</ul>

</details>

---

