# Sistema de RH - POO II

Trabalho desenvolvido por:
- Guilherme Gomes de Oliveira  
- Leonardo Dumes de Souza  

---

## 📊 Diagrama UML

```plantuml
@startuml

abstract class Pessoa {
    - id : int
    - nome : String
    - cpf : String
    - dataNascimento : LocalDate
}

class Funcionario {
    - id : int
    - salarioBruto : double
    - setor : Setor
    + calcularInss() : double
    + calcularIrpf() : double
    + calcularSalarioLiquido() : double
}

class Setor {
    - id : int
    - nome : String
    - ramal : int
}

class Movimentacao {
    - id : int
    - tipo : TipoMovimentacao
    - data : LocalDateTime
    - valor : double
    - descricao : String
}

class Usuario {
    - id : int
    - login : String
    - senha : String
}

enum TipoMovimentacao {
    RECEITA
    DESPESA
}

Pessoa <|-- Funcionario
Setor "1" -- "0..*" Funcionario
Setor "1" -- "0..*" Movimentacao
Setor "1" -- "0..*" Usuario

@enduml
```

---
