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
<summary><h2>📐 Diagrama UML</h2></summary>

<p>
Este diagrama representa el modelo de dominio y las relaciones entre entidades
principales del sistema.
</p>

<!-- Aquí va la imagen del diagrama UML -->



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
  <li>Java 21</li>
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
Spring Boot:    4.0.2
Java:           21
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
  <li><strong>Users</strong> – Gestión de usuarios y roles</li>
  <li><strong>Auth / JWT</strong> – Autenticación y seguridad</li>
  <li><strong>Equipment</strong> – Gestión de equipos médicos</li>
  <li><strong>Inventory</strong> – Control de inventarios</li>
  <li><strong>Maintenance</strong> – Mantenimientos y programación</li>
  <li><strong>Reports & Audit</strong> – Reportes y auditoría</li>
</ul>

</details>

---

<details>
<summary><h2>👤 Users (Gestión de Usuarios)</h2></summary>

<h3>📖 Descripción</h3>

<p>
Este microservicio es responsable de la gestión de usuarios del sistema, incluyendo:
</p>

<ul align="left">
  <li>Creación, actualización y desactivación de usuarios</li>
  <li>Asignación de roles definidos por el sistema</li>
  <li>Validación de datos críticos antes de su persistencia</li>
  <li>Control del ciclo de vida del usuario</li>  
</ul>

<br>

<p>
<strong>⚠️ Este microservicio NO maneja autenticación ni emisión de tokens.</strong>
</p>

---


<h3>🔄 Flujo General</h3>
<h2><strong>(Viaje de los datos dentro del sistema)</strong></h2>

<p>Este microservicio sigue una <strong>arquitectura en capas</strong>, donde los datos viajan de forma controlada desde la entrada (HTTP) hasta la base de datos y de regreso al cliente.</p>

```bash
Cliente
  ↓
Controller
  ↓
DTO Request
  ↓
Service (reglas de negocio)
  ↓
Util / Constants
  ↓
Repository
  ↓
Entity
  ↓
DTO Response
  ↓
Cliente

```

---

<h3>📦 Endpoints principales</h3>

<table>
  <tr>
    <th>Endpoint</th>
    <th>Descripción</th>
  </tr>
  <tr>
    <td><strong>POST **Ruta:** `/users-create`</strong></td>
    <td>Creación de un nuevo usuario</td>
  </tr>
  <tr>
    <td><strong>PATCH **Ruta:** `/edit-user/{id}`</strong></td>
    <td>Edita la informacion de un usuario guardando el campo cambiado sin cambiar todo el objeto</td>
  </tr>
  <tr>
    <td><strong>GET **Ruta:** `/get-all-users`</strong></td>
    <td>Obtiene todos los usuarios</td>
  </tr>
  <tr>
    <td><strong>GET **Ruta:** `/get-user-by-id/{id}`</strong></td>
    <td>Obtiene un usuario por su id</td>
  </tr>
  <tr>
    <td><strong>GET **Ruta:** `/get-user-by-email/sigebi@sigebi.com`</strong></td>
    <td>Obtiene la información de un usuario por su email</td>
  </tr>
  <tr>
    <td><strong>PATCH **Ruta:** `/deactive-user/{id}`</strong></td>
    <td>Desactiva un usuario - soft delete</td>
  </tr>
  <tr>
    <td><strong>PATCH **Ruta:** `/activate-user/{id}`</strong></td>
    <td>Activa un usuario que estaba desactivado</td>
  </tr>
  <tr>
    <td><strong>DELETE **Ruta:** `/deletehard-user/{id}`</strong></td>
    <td>Elimina un usuario definitivamente - hard delete</td>
  </tr>
</table>

---

<h3>⚙️ Validaciones y decisiones técnicas</h3>

<ul align="left">
  <li>✔ Validación de unicidad de <strong>correo electrónico</strong></li>
  <li>✔ Validación de unicidad de <strong>número telefónico</strong></li>
  <li>✔ Validación de existencia de <strong>IdRol</strong></li>
  <li>✔ Validación de existencia de <strong>IdEntidad</strong></li>
  <li>✔ Validación de fecha de nacimiento (no fechas futuras)</li>
  <li>✔ Validación de edad mínima permitida</li>
  <li>✔ Normalización de correo electrónico (trim, lowercase)</li>
  <li>✔ Prevención de registros con campos vacíos o inválidos</li>  
</ul>

---

<h3>🧪 Pruebas</h3>

<ul align="left">
  <li>Pruebas unitarias</li>
  <li>Pipeline CI obligatorio para merge</li>
</ul>

</details>
