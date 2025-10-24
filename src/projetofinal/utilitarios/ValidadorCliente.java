package projetofinal.utilitarios;

public class ValidadorCliente {

        public static void isClienteValido(String nome, String cpf, String endereco) {
            if (nome == null || nome.isBlank()) {
                throw new IllegalArgumentException("O campo Nome nao pode ser vazio");
            }
            if (nome.matches(".*\\d.*")) {
                throw new IllegalArgumentException("O campo Nome nao pode conter números");
            }
            if (nome.matches(".*[^a-zA-ZÀ-ú ]+.*")) {
                throw new IllegalArgumentException("O campo Nome contem caracteres inválidos");
            }

            if (cpf == null || cpf.isBlank() || !ValidadorCliente.isCPFValido(cpf)) {
                throw new IllegalArgumentException("O campo CPF está incorreto");
            }

            if (endereco == null || endereco.isBlank()) {
                throw new IllegalArgumentException("O campo Endereço nao pode ser vazio");
            }
            if (endereco.length() > 120) {
                throw new IllegalArgumentException("O campo Endereço deve ter ate 120 caracteres");
            }

        }

        public static boolean isCPFValido(String cpf) {
            cpf = cpf.replaceAll("[^\\d]", "");

            if (cpf.length() != 11) {
                return false;
            }

            if (cpf.matches("(\\d)\\1{10}")) {
                return false;
            }

            try {
                int soma = 0;
                for (int i = 0; i < 9; i++) {
                    int num = Character.getNumericValue(cpf.charAt(i));
                    soma += num * (10 - i);
                }

                int primeiroDigito = 11 - (soma % 11);
                if (primeiroDigito >= 10) {
                    primeiroDigito = 0;
                }

                soma = 0;
                for (int i = 0; i < 10; i++) {
                    int num = Character.getNumericValue(cpf.charAt(i));
                    soma += num * (11 - i);
                }

                int segundoDigito = 11 - (soma % 11);
                if (segundoDigito >= 10) {
                    segundoDigito = 0;
                }

                return cpf.charAt(9) == Character.forDigit(primeiroDigito, 10)
                        && cpf.charAt(10) == Character.forDigit(segundoDigito, 10);

            } catch (Exception e) {
                return false;
            }
        }
    }
