# InkMap-API
### Requisitos
- JDK: ms-21 Microsoft OpenJDK 21.0.10
- Java: 21

## Flujo de trabajo
El proyecto seguirá el siguiente flujo de trabajo:
1. Crear una nueva rama por cada historia de usuario tomando `dev` como base. El nombre de la rama deberá seguir este formato
    * `[nombre-de-desarrollador]-[descripción-breve-de-funcionalidad]`

2. Al terminar la funcionalidad se hará un pull request a la rama `test` con una descripción de qué se está implementando
   
4. El pull request a `test` será autorizado (code review) por el coordinador de soporte (Jose Daniel Steller) o desarrollo (Axel Jiménez)
   
6. Otro desarrollador deberá crear los casos de prueba utilizando la [plantilla del drive](https://docs.google.com/spreadsheets/d/1fzYvGnlQ998Z2fwOR1Gk8WeX80EbtQQENgHlmDPivtw/edit?usp=drive_link)
   
8. El archivo se adjuntará en la historia de usuario en Jira
   
10. Cuando los casos de prueba estén listos se realizará otro pull request a la rama `main` con los mismos requisitos de los pull requests a `test`
