package cli;

import dominio.Cliente;
import dominio.Conta;
import dominio.ContaCorrente;
import dominio.ContaPoupanca;
import dominio.Transacao;
import excecao.ContaJaCadastradaException;
import excecao.ContaNaoEncontradaException;
import excecao.SaldoInsuficienteException;
import excecao.ValorInvalidoException;
import service.BancoService;
import util.DataHoraUtil;
import util.DinheiroUtil;

import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final BancoService bancoService = new BancoService();

    public static void main(String[] args) {
        boolean rodando = true;

        while (rodando) {
            exibirMenu();
            int opcao = lerOpcaoInteira("Escolha uma opção: ");

            try {
                switch (opcao) {
                    case 1 -> abrirContaCorrente();
                    case 2 -> abrirContaPoupanca();
                    case 3 -> realizarDeposito();
                    case 4 -> realizarSaque();
                    case 5 -> realizarTransferencia();
                    case 6 -> consultarSaldo();
                    case 7 -> consultarExtrato();
                    case 8 -> aplicarRendimento();
                    case 0 -> {
                        System.out.println("\nEncerrando o sistema. Até logo!");
                        rodando = false;
                    }
                    default -> System.out.println("Opção inválida! Escolha um valor do menu.");
                }
            } catch (SaldoInsuficienteException | ValorInvalidoException |
                     ContaJaCadastradaException | ContaNaoEncontradaException |
                     IllegalStateException e) {
                System.out.println("\n[ERRO DE NEGÓCIO]: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("\n[ERRO INESPERADO]: Ocorreu uma falha no processamento.");
                e.printStackTrace();
            }

            if (rodando) {
                System.out.println("\nPressione ENTER para voltar ao menu...");
                scanner.nextLine();
            }
        }
    }

    private static void exibirMenu() {
        System.out.println("\n=========================================");
        System.out.println("       SISTEMA BANCARIO DIGITAL CLI      ");
        System.out.println("=========================================");
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

    private static void abrirContaCorrente() {
        Cliente cliente = coletarDadosCliente();
        int numero = bancoService.gerarNovoNumeroConta();
        long limiteCentavos = 50000L; // R$ 500,00 de limite padrão

        ContaCorrente cc = new ContaCorrente(numero, cliente, limiteCentavos);
        bancoService.cadastrarConta(cc);
        System.out.printf("Conta Corrente nº %d criada com sucesso! Limite cheque especial: %s%n",
                numero, DinheiroUtil.formatar(limiteCentavos));
    }

    private static void abrirContaPoupanca() {
        Cliente cliente = coletarDadosCliente();
        int numero = bancoService.gerarNovoNumeroConta();

        ContaPoupanca cp = new ContaPoupanca(numero, cliente);
        bancoService.cadastrarConta(cp);
        System.out.printf("Conta Poupança nº %d criada com sucesso!%n", numero);
    }

    private static Cliente coletarDadosCliente() {
        System.out.print("Nome do titular: ");
        String nome = scanner.nextLine().strip();
        System.out.print("CPF (apenas 11 dígitos): ");
        String cpf = scanner.nextLine().strip();
        return new Cliente(nome, cpf);
    }

    private static void realizarDeposito() {
        int numero = lerOpcaoInteira("Número da conta: ");
        Conta conta = bancoService.buscarPorNumero(numero);

        System.out.print("Valor do depósito (ex: 50.00): R$ ");
        long centavos = DinheiroUtil.converterParaCentavos(scanner.nextLine());
        conta.depositar(centavos);
        System.out.println("Depósito realizado com sucesso!");
    }

    private static void realizarSaque() {
        int numero = lerOpcaoInteira("Número da conta: ");
        Conta conta = bancoService.buscarPorNumero(numero);

        System.out.print("Valor do saque (ex: 30.00): R$ ");
        long centavos = DinheiroUtil.converterParaCentavos(scanner.nextLine());
        conta.sacar(centavos);
        System.out.println("Saque efetuado com sucesso!");
    }

    private static void realizarTransferencia() {
        int origem = lerOpcaoInteira("Número da sua conta: ");
        int destino = lerOpcaoInteira("Número da conta de destino: ");

        System.out.print("Valor da transferência (ex: 100.00): R$ ");
        long centavos = DinheiroUtil.converterParaCentavos(scanner.nextLine());

        bancoService.transferir(origem, destino, centavos);
        System.out.println("Transferência realizada com sucesso!");
    }

    private static void consultarSaldo() {
        int numero = lerOpcaoInteira("Número da conta: ");
        Conta conta = bancoService.buscarPorNumero(numero);
        System.out.printf("Titular: %s | Saldo Atual: %s%n",
                conta.getTitular().nome(),
                DinheiroUtil.formatar(conta.getSaldoCentavos()));
    }

    private static void consultarExtrato() {
        int numero = lerOpcaoInteira("Número da conta: ");
        Conta conta = bancoService.buscarPorNumero(numero);

        System.out.println("\n========== EXTRATO BANCÁRIO ==========");
        System.out.println("Titular: " + conta.getTitular().nome());
        System.out.println("CPF: " + mascararCpf(conta.getTitular().cpf()));
        System.out.println("Saldo: " + DinheiroUtil.formatar(conta.getSaldoCentavos()));
        System.out.println("--------------------------------------");

        if (conta.getTransacoes().isEmpty()) {
            System.out.println("Nenhuma movimentação registrada.");
        } else {
            for (Transacao t : conta.getTransacoes()) {
                System.out.printf("[%s] %-22s : %s%n",
                        DataHoraUtil.formatar(t.dataHora()),
                        t.tipo(),
                        DinheiroUtil.formatar(t.valorCentavos()));
            }
        }
        System.out.println("======================================");
    }

    private static void aplicarRendimento() {
        int numero = lerOpcaoInteira("Número da Conta Poupança: ");
        Conta conta = bancoService.buscarPorNumero(numero);

        if (conta instanceof ContaPoupanca cp) {
            cp.aplicarRendimentoMensal();
            System.out.println("Rendimento de 0.5% aplicado com sucesso!");
            System.out.println("Novo saldo: " + DinheiroUtil.formatar(cp.getSaldoCentavos()));
        } else {
            System.out.println("A conta informada não é uma Conta Poupança.");
        }
    }

    private static int lerOpcaoInteira(String mensagem) {
        while (true) {
            System.out.print(mensagem);
            String input = scanner.nextLine().strip();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida! Digite apenas números inteiros.");
            }
        }
    }

    private static String mascararCpf(String cpf) {
        if (cpf == null || cpf.length() != 11) {
            return "***.***.***-**";
        }
        return cpf.substring(0, 3) + ".***.***-" + cpf.substring(9);
    }
}