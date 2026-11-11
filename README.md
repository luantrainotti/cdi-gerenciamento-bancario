# CDI - Sistema de Gerenciamento Bancário (CLI)

Aplicação de gestão bancária em linha de comandos (CLI) desenvolvida em **Java 17+ puro**, focada em boas práticas de Programação Orientada a Objetos, integridade financeira (*Money Pattern*), concorrência e estruturas de dados em memória.

---

## 🚀 Tecnologias e Decisões de Arquitetura

* **Java 17+ (Core puro):** Implementado sem dependências ou *frameworks* externos para evidenciar o domínio da linguagem base.
* **Integridade Financeira (*Money Pattern*):** Representação monetária estritamente em cêntimos utilizando o tipo primitivo `long`, eliminando erros de arredondamento de vírgula flutuante (`double`/`float`) e alocações excessivas de objetos em memória. A conversão de texto e exibição visual é tratada na camada utilitária (`DinheiroUtil`).
* **Imutabilidade e Encapsulamento:**
    * Uso de **Records** para modelagem de entidades imutáveis com validação compacta (`Cliente` e `Transacao`).
    * Saldo com visibilidade estritamente `private` e histórico de transações exposto através de *snapshots* imutáveis (`List.copyOf`).
* **Concorrência e Prevenção de Deadlocks:**
    * Operações de transferência protegidas através de sincronização encadeada com bloqueios ordenados (*ordered locks*) baseados no identificador das contas, garantindo atomicidade sem risco de bloqueio mútuo.
    * Estruturas em memória geridas com coleções concorrentes (`ConcurrentHashMap`).
* **Indexação em Tempo Constante $O(1)$:**
    * Dupla indexação em memória mapeando tanto o número da conta quanto o CPF normalizado para consultas e validações de unicidade em $O(1)$.
* **Tratamento de Exceções Semântico:**
    * Hierarquia de exceções de negócio personalizadas (`SaldoInsuficienteException`, `ContaJaCadastradaException`, etc.) operando no modelo *fail-fast*.
* **Controlo de Rendimento Mensal:**
    * Proteção através da API `java.time.YearMonth`, impedindo múltiplas aplicações de juros na poupança no mesmo ciclo mensal.

---

## 📁 Estrutura de Pacotes

```text
src/
├── cli/
│   └── Main.java                      # Interface interativa CLI e tratamento de exceções
├── dominio/
│   ├── Cliente.java                   # Record imutável com normalização de CPF
│   ├── Conta.java                     # Classe base abstrata com regras de saldo e extrato
│   ├── ContaCorrente.java             # Subclasse com cheque especial e taxa de operação
│   ├── ContaPoupanca.java             # Subclasse com taxa de rendimento e guarda de ciclo mensal
│   ├── TipoTransacao.java             # Enum com tipos de movimentação
│   └── Transacao.java                 # Record imutável da movimentação financeira
├── excecao/
│   ├── ContaJaCadastradaException.java
│   ├── ContaNaoEncontradaException.java
│   ├── SaldoInsuficienteException.java
│   └── ValorInvalidoException.java
├── service/
│   └── BancoService.java              # Regras de negócio, indexação O(1) e transferências atómicas
└── util/
    ├── DataHoraUtil.java              # Formatador reutilizável de datas (dd/MM/yyyy HH:mm:ss)
    └── DinheiroUtil.java              # Conversão segura de String para cêntimos em long e formatação
```

---

## ⚙️ Como Executar

1. **Pré-requisitos:** Ter o JDK 17 (ou superior) e o Git instalados.
2. **Clonar o repositório:**
```bash
git clone [https://github.com/luantrainotti/cdi-gerenciamento-bancario.git](https://github.com/luantrainotti/cdi-gerenciamento-bancario.git)
cd cdi-gerenciamento-bancario
```
3. **Compilar o projeto:**
* No Linux / macOS / Git Bash:
```bash
javac -d out $(find src -name "*.java")
```
* No Windows (CMD / PowerShell):
```cmd
javac -d out src/cli/*.java src/dominio/*.java src/excecao/*.java src/service/*.java src/util/*.java
```
4. **Executar a aplicação:**
```bash
java -cp out cli.Main
```

---

## 📄 Licença

Distribuído sob a licença MIT. Consulta o ficheiro `LICENSE` para mais detalhes.