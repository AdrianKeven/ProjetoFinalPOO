# 🏦 Sistema Bancário em Java

![Java](https://img.shields.io/badge/Java-17-orange?logo=java&logoColor=white)  
![Build](https://img.shields.io/badge/Build-Passing-brightgreen?style=flat-square)  
![License](https://img.shields.io/badge/License-Educational-blue?style=flat-square)  
![Status](https://img.shields.io/badge/Status-Em%20Desenvolvimento-yellow?style=flat-square)  

## 📌 Sobre o projeto
Este projeto implementa um **sistema bancário orientado a objetos em Java**, com suporte a clientes, contas correntes e poupança, além de operações como depósitos, saques, transferências e aplicação de juros.  
O código aplica conceitos de **POO** como **herança, polimorfismo, encapsulamento** e **tratamento de exceções personalizadas**.

## 🗂 Estrutura do projeto
src/  
└── projetofinal/  
&emsp; ├── entidades/  
&emsp; │   ├── Cliente.java  
&emsp; │   ├── Conta.java  
&emsp; │   ├── ContaCorrente.java  
&emsp; │   └── ContaPoupanca.java  
&emsp; │  
&emsp; ├── utilitarios/  
&emsp; │   ├── ValidadorCliente.java  
&emsp; │   └── SaldoInsuficienteException.java  
&emsp; │  
&emsp; └── ProjetoFinal.java  

## ⚙️ Funcionalidades
- 👤 **Cliente**
  - Cadastro com validação de nome, CPF e endereço
  - Associação de múltiplas contas
- 💳 **Conta Corrente**
  - Depósitos, saques e transferências
  - Uso de **cheque especial** como limite extra
- 💰 **Conta Poupança**
  - Depósitos, saques e transferências
  - Aplicação de **juros mensais automáticos**
- 🚨 **Validações**
  - CPF válido
  - Nome sem números ou caracteres inválidos
  - Endereço com limite de caracteres
- 📜 **Histórico de transações**
  - Cada operação é registrada em uma lista de transações

## 🖥️ Exemplo de uso
public class ProjetoFinal {  
&emsp; public static void main(String[] args) {  
&emsp;&emsp; Cliente cliente1 = new Cliente("João", "12345678909", "Rua A, 123", new ArrayList<>());  
&emsp;&emsp; Cliente cliente2 = new Cliente("Maria", "98765432100", "Rua B, 456", new ArrayList<>());  

&emsp;&emsp; Conta conta1 = new ContaCorrente("001", cliente1, 500.0);  
&emsp;&emsp; Conta conta2 = new ContaPoupanca("002", cliente2);  

&emsp;&emsp; cliente1.adicionarConta(conta1);  
&emsp;&emsp; cliente2.adicionarConta(conta2);  

&emsp;&emsp; conta1.depositar(1000.0);  
&emsp;&emsp; conta1.transferir(conta2, 200.0);  

&emsp;&emsp; System.out.println(conta1);  
&emsp;&emsp; System.out.println(conta2);  
&emsp; }  
}  

## 🛠️ Tecnologias utilizadas
- ☕ **Java 17+**
- 🏗 **POO (Herança, Polimorfismo, Encapsulamento)**
- ⚡ **Exceções personalizadas**
- 📂 **Organização modular em pacotes**

## 🚀 Como executar
1. Clone o repositório:  
   git clone https://github.com/seu-usuario/sistema-bancario.git  

2. Compile os arquivos:  
   javac src/projetofinal/**/*.java  

3. Execute o programa:  
   java src/projetofinal/ProjetoFinal  

## 🧹 .gitignore recomendado
# IntelliJ IDEA  
*.iml  
.idea/  

# Eclipse  
.project  
.classpath  
.settings/  

# VS Code  
.vscode/  

# Builds/temporários  
/out/  
/bin/  
/target/  
*.log  

## 🗺️ Roadmap
- ✅ Estrutura base de entidades (Cliente, Conta, Corrente, Poupança)  
- ✅ Validador de cliente e exceções personalizadas  
- ✅ Histórico de transações  
- ⏳ Testes unitários (JUnit)  
- ⏳ Persistência em arquivo/JSON  
- ⏳ Interface CLI mais amigável  

## 🤝 Contribuição
1. Crie uma branch: `feature/nome-da-feature`  
2. Faça suas alterações e commit  
3. Abra um Pull Request descrevendo mudanças e testes  

## 📜 Licença
Projeto de uso **educacional**. Livre para estudo, modificação e melhorias. 🎓  
