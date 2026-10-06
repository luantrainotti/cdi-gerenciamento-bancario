package cli;

import dominio.Conta;
import dominio.ContaCorrente;
import dominio.ContaPoupanca;
import dominio.Transacao;
import excecao.ContaJaCadastradaException;
import excecao.ContaNaoEncontradaException;
import excecao.SaldoInsuficienteException;
import excecao.ValorInvalidoException;
import service.BancoService;

import java.math.BigDecimal;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final BancoService bancoService = new BancoService();

    public static void main(String[] args) {
        int opcao = -1;

        System.out.println("=========================================");
        System.out.println("      SISTEMA BANCARIO DIGITAL CLI       ");
        System.out.println("=========================================");

        do {
            exibirMenu();
            opcao = lerOpcaoInteira("Escolha uma opção: ");

            try {
                switch (opcao) {
                    case 1 -> criarContaCorrente();
                    case 2 -> criarContaPoupanca();
                    case 3 -> realizarDeposito();
                    case 4 -> realizarSaque();
                    case 5 -> realizarTransferencia();
                    case 6 -> consultarSaldo();
                    case 7 -> consultarExtrato();
                    case 8 -> aplicarRendimentoPoupanca();
                    case 0 -> System.out.println("\nEncerrando o sistema... Obrigado por utilizar!");
                    default -> System.err.println("Opção inválida! Selecione uma opção válida do menu.");
                }
            } catch (SaldoInsuficienteException | ValorInvalidoException | ContaNaoEncontradaException | ContaJaCadastradaException e) {
                System.err.println("\n[ERRO DE NEGÓCIO] " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.err.println("\n[DADO INVÁLIDO] " + e.getMessage());
            } catch (Exception e) {
                System.err.println("\n[ERRO INESPERADO] Ocorreu uma falha: " + e.getMessage());
            }

            if (opcao != 0) {
                System.out.println("\nPressione ENTER para continuar...");
                scanner.nextLine();
            }

        } while (opcao != 0);

        scanner.close();
    }

    private static void exibirMenu() {
        System.out.println("\n---------------- MENU -------------------");
        System.out.println("1 - Abrir Conta Corrente");
        System.out.println("2 - Abrir Conta Poupança");
        System.out.println("3 - Realizar Depósito");
        System.out.println("4 - Realizar Saque");
        System.out.println("5 - Transferir entre Contas");
        System.out.println("6 - Consultar Saldo");
        System.out.println("7 - Consultar Extrato");
        System.out.println("8 - Aplicar Rendimento (Poupança)");
        System.out.println("0 - Sair");
        System.out.println("-----------------------------------------");
    }

    private static void criarContaCorrente() {
        System.out.println("\n--- Abertura de Conta Corrente ---");
        System.out.print("Nome do titular: ");
        String nome = scanner.nextLine();
        System.out.print("CPF: ");
        String cpf = scanner.nextLine();
        System.out.print("E-mail: ");
        String email = scanner.nextLine();
        BigDecimal limite = lerValorBigDecimal("Limite do cheque especial (R$): ");

        ContaCorrente cc = bancoService.abrirContaCorrente(nome, cpf, email, limite);
        System.out.printf("Conta Corrente nº %s aberta com sucesso para %s!%n", cc.getNumero(), cc.getTitular().getNome());
    }

    private static void criarContaPoupanca() {
        System.out.println("\n--- Abertura de Conta Poupança ---");
        System.out.print("Nome do titular: ");
        String nome = scanner.nextLine();
        System.out.print("CPF: ");
        String cpf = scanner.nextLine();
        System.out.print("E-mail: ");
        String email = scanner.nextLine();

        ContaPoupanca cp = bancoService.abrirContaPoupanca(nome, cpf, email);
        System.out.printf("Conta Poupança nº %s aberta com sucesso para %s!%n", cp.getNumero(), cp.getTitular().getNome());
    }

    private static void realizarDeposito() {
        System.out.println("\n--- Depósito ---");
        System.out.print("Número da conta: ");
        String numero = scanner.nextLine();
        BigDecimal valor = lerValorBigDecimal("Valor do depósito (R$): ");

        Conta conta = bancoService.buscarConta(numero);
        conta.depositar(valor);
        System.out.printf("Depósito realizado com sucesso! Novo saldo: R$ %.2f%n", conta.getSaldo());
    }

    private static void realizarSaque() {
        System.out.println("\n--- Saque ---");
        System.out.print("Número da conta: ");
        String numero = scanner.nextLine();
        BigDecimal valor = lerValorBigDecimal("Valor do saque (R$): ");

        Conta conta = bancoService.buscarConta(numero);
        conta.sacar(valor);
        System.out.printf("Saque realizado com sucesso! Saldo atual: R$ %.2f%n", conta.getSaldo());
    }

    private static void realizarTransferencia() {
        System.out.println("\n--- Transferência Bancária ---");
        System.out.print("Número da conta de ORIGEM: ");
        String origem = scanner.nextLine();
        System.out.print("Número da conta de DESTINO: ");
        String destino = scanner.nextLine();
        BigDecimal valor = lerValorBigDecimal("Valor da transferência (R$): ");

        bancoService.transferir(origem, destino, valor);
        System.out.println("Transferência concluída com sucesso!");
    }

    private static void consultarSaldo() {
        System.out.println("\n--- Consulta de Saldo ---");
        System.out.print("Número da conta: ");
        String numero = scanner.nextLine();

        Conta conta = bancoService.buscarConta(numero);
        System.out.printf("Titular: %s%n", conta.getTitular().getNome());
        System.out.printf("Saldo disponível: R$ %.2f%n", conta.getSaldo());
        if (conta instanceof ContaCorrente cc) {
            System.out.printf("Limite cheque especial: R$ %.2f%n", cc.getLimiteChequeEspecial());
            System.out.printf("Saldo total com limite: R$ %.2f%n", cc.getSaldo().add(cc.getLimiteChequeEspecial()));
        }
    }

    private static void consultarExtrato() {
        System.out.println("\n--- Extrato da Conta ---");
        System.out.print("Número da conta: ");
        String numero = scanner.nextLine();

        Conta conta = bancoService.buscarConta(numero);
        System.out.println("---------------------------------------------------------------------------------");
        System.out.printf("Extrato - Conta: %s | Titular: %s (CPF: %s)%n",
                conta.getNumero(), conta.getTitular().getNome(), conta.getTitular().getCpf());
        System.out.println("---------------------------------------------------------------------------------");

        if (conta.getTransacoes().isEmpty()) {
            System.out.println("Nenhuma movimentação registrada até o momento.");
        } else {
            for (Transacao t : conta.getTransacoes()) {
                System.out.println(t);
            }
        }
        System.out.println("---------------------------------------------------------------------------------");
        System.out.printf("Saldo final atual: R$ %.2f%n", conta.getSaldo());
    }

    private static void aplicarRendimentoPoupanca() {
        System.out.println("\n--- Rendimento de Poupança ---");
        System.out.print("Número da conta poupança: ");
        String numero = scanner.nextLine();

        Conta conta = bancoService.buscarConta(numero);
        if (!(conta instanceof ContaPoupanca cp)) {
            throw new ValorInvalidoException("A conta informada não é uma Conta Poupança.");
        }

        cp.aplicarRendimento();
        System.out.printf("Rendimento de %.2f%% aplicado com sucesso! Novo saldo: R$ %.2f%n",
                ContaPoupanca.getTaxaRendimento(), cp.getSaldo());
    }

    private static int lerOpcaoInteira(String mensagem) {
        while (true) {
            System.out.print(mensagem);
            String input = scanner.nextLine().strip();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.err.println("Entrada inválida! Digite apenas números inteiros.");
            }
        }
    }

    private static BigDecimal lerValorBigDecimal(String mensagem) {
        while (true) {
            System.out.print(mensagem);
            String input = scanner.nextLine().strip().replace(",", ".");
            try {
                return new BigDecimal(input);
            } catch (NumberFormatException e) {
                System.err.println("Valor numérico inválido! Use formato como '150.00' ou '150,00'.");
            }
        }
    }
}