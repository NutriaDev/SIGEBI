<div align="center">

<h1>SIGEBI</h1>
<h3>Sistema de Interfaz de Gestión de Equipos Biomédicos</h3>

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
Este microservicio es responsable de la gestión de usuarios del sistema,
incluyendo roles, permisos, autenticación y control de acceso.
</p>

---

<h3>🎯 Flujo General</h3>

<p>
El flujo principal cubre la creación, autenticación, habilitación/deshabilitación
y auditoría de usuarios.
</p>

---

<h3>📦 Endpoints principales</h3>

<table>
  <tr>
    <th>Endpoint</th>
    <th>Descripción</th>
  </tr>
  <tr>
    <td><strong>POST /users</strong></td>
    <td>Creación de un nuevo usuario</td>
  </tr>
  <tr>
    <td><strong>GET /users/{id}</strong></td>
    <td>Obtiene la información de un usuario</td>
  </tr>
</table>

---

<h3>⚙️ Validaciones y decisiones técnicas</h3>

<ul align="left">
  <li>✔ Emisión de <strong>Access Token</strong> y <strong>Refresh Token</strong></li>
  <li>✔ Rotación segura de Refresh Tokens</li>
  <li>✔ Revocación individual de sesión (logout)</li>
  <li>✔ Prevención de <em>refresh token replay attacks</em></li>
  <li>✔ Cookies <code>httpOnly</code> con <code>SameSite=Strict</code></li>
  <li>✔ Hash de Refresh Tokens con <strong>Argon2</strong></li>
  <li>✔ Uso de identificadores únicos (<code>tid</code>)</li>
  <li>✔ JWT <strong>stateless</strong> y payload mínimo</li>
</ul>

---

<h3>🧪 Pruebas</h3>

<ul align="left">
  <li>Pruebas unitarias</li>
  <li>Pruebas de integración</li>
  <li>Pipeline CI obligatorio para merge</li>
</ul>

</details>
