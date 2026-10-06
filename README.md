# Sistema Bancário CLI em Java

Aplicação de consola desenvolvida em Java puro com foco na prática intensiva dos pilares de **Programação Orientada a Objetos (POO)**, modelação de domínio, tratamento robusto de exceções personalizadas e manipulação precisa de cálculos financeiros.

---

## 🛠️ Tecnologias e Decisões Técnicas

* **Java 17+**: Utilização de recursos modernos como *Pattern Matching* para `instanceof`, *Switch Expressions* e métodos utilitários de texto.
* **`BigDecimal` & `RoundingMode.HALF_UP`**: Cálculos monetários seguros, evitando os problemas clássicos de arredondamento de vírgula flutuante binária com `double` e `float`.
* **Armazenamento em Memória com `Map`**: Estrutura `HashMap` a gerir contas e clientes, garantindo pesquisas eficientes em tempo constante $O(1)$.
* **Higienização de Dados**: Utilização de `.strip()` e validações estritas de contrato (*fail-fast*) através de construtores e métodos de domínio.
* **Histórico com Conventional Commits**: Commits atómicos e padronizados de acordo com a especificação técnica.

---

## 🏛️ Arquitetura e Estrutura de Pacotes

O projeto segue a separação de responsabilidades em camadas lógicas:

```text
src/
├── cli/
│   └── Main.java                      # Ponto de entrada, menu interativo e captura de exceções
├── dominio/
│   ├── Cliente.java                   # Entidade titular imutável com validação de campos
│   ├── Conta.java                     # Classe abstrata base com saldo e histórico de transações
│   ├── ContaCorrente.java             # Subclasse com taxa de operação e cheque especial
│   ├── ContaPoupanca.java             # Subclasse com taxa de rendimento mensal fixa (0.50%)
│   ├── TipoTransacao.java             # Enum com tipos de movimentações financeiras
│   └── Transacao.java                 # Registo de extrato com data, hora e valor imutáveis
├── excecao/
│   ├── ContaJaCadastradaException.java
│   ├── ContaNaoEncontradaException.java
│   ├── SaldoInsuficienteException.java
│   └── ValorInvalidoException.java
└── service/
    └── BancoService.java              # Gestão de regras, repositório em memória e transferências
```
---

## 💡 Princípios de POO Aplicados

* **Abstração & Polimorfismo**: A classe abstrata `Conta` define o contrato base das movimentações. As subclasses `ContaCorrente` e `ContaPoupanca` sobrepõem o método `sacar(BigDecimal valor)` aplicando as suas regras de negócio específicas.
* **Encapsulamento**: Os saldos e dados sensíveis não podem ser alterados diretamente via métodos de modificação (*setters*). O histórico de extrato é disponibilizado apenas como uma lista imutável com `Collections.unmodifiableList()`.
* **Reutilização de Entidades**: A camada de serviço garante que o mesmo cliente (identificado por CPF único) utilize a mesma referência em memória ao associar mais do que uma conta, prevenindo dados duplicados.
* **Operação Atómica**: As transferências bancárias efetuam débito na conta de origem e crédito na de destino de forma encadeada; se a origem não tiver fundos, a operação é interrompida sem alterar a conta de destino.

---

## ⚙️ Regras de Negócio

| Funcionalidade | Conta Corrente | Conta Poupança |
| --- | --- | --- |
| **Taxa de Levantamento** | R$ 2,50 por operação | Isento |
| **Cheque Especial** | Limite configurável na abertura | Não possui |
| **Rendimento** | Não aplicável | Taxa fixa de 0,50% ao mês com arredondamento comercial |
| **Transferência** | Valida saldo + limite | Valida apenas saldo disponível |
| **Unicidade** | Máximo de 1 conta por CPF | Máximo de 1 conta por CPF |

---

## 🚀 Como Executar

### Pré-requisitos

* **JDK 17** ou superior instalado.
* Git configurado no sistema.

### Passos

1. Clone o repositório:

```bash
git clone [https://github.com/SEU_USUARIO/cdi-gerenciamento-bancario.git](https://github.com/SEU_USUARIO/cdi-gerenciamento-bancario.git)
```

2. Aceda à pasta do projeto:

```bash
cd cdi-gerenciamento-bancario
```

3. Compile os ficheiros Java:

```bash
javac -d bin src/**/*.java
```

4. Inicie o sistema no terminal:

```bash
java -cp bin cli.Main
