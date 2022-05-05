package com.G3.scm.servico;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.autoconfigure.mail.MailSenderValidatorAutoConfiguration;

import com.G3.scm.model.Cliente;
import com.G3.scm.model.ClienteRepository;
import com.G3.scm.model.Endereco;
import com.G3.scm.model.EnderecoRepository;

@Service
public  class ClienteServicoI implements ClienteServico {
	Logger logger = LogManager.getLogger(ClienteServicoI.class);
	
	@Autowired
	private ClienteRepository clienteRepository;
	@Autowired
	private EnderecoRepository enderecoRepository;
	@Autowired
	private JavaMailSender mailSender; 
	@Autowired

	public Iterable<Cliente> findAll() {
		return clienteRepository.findAll();
	}

	public Cliente findByCpf(String cpf) {
		return clienteRepository.findByCpf(cpf);
	}

	public void deleteById(Long id) {
		clienteRepository.deleteById(id);
		logger.info(">>>>>> 2. comando exclusao executado para o id => " + id);
	}

	public Cliente findById(Long id) {
		return clienteRepository.findById(id).get();
	}

	public  ModelAndView save (Cliente cliente) {
		ModelAndView modelAndView = new ModelAndView("consultarCliente");
		cliente.setDtNascimento(cliente.getDtNascimentoFormat());
		DateTime anoAtual = new DateTime();
		int idade = anoAtual.getYear() - cliente.getDtNascimento().getYear();
		
			Endereco endereco = obtemEndereco(cliente.getCep());

			if (endereco != null && idade >= 18) {
				cliente.setDataCadastro(new DateTime());

				endereco.setCpf(cliente.getCpf());
				enderecoRepository.save(endereco);
				cliente.setEndereco(endereco);
				cliente.getEndereco().setNum(cliente.getNum());

				clienteRepository.save(cliente);
				logger.info(">>>>>> 4. comando save executado  ");
				sendMail(cliente);
				modelAndView.addObject("clientes", clienteRepository.findAll());
			}

			else {
				modelAndView.setViewName("cadastrarCliente");
				modelAndView.addObject("message", "Precisa ser maior de 18 Anos, Zé.");

				logger.info(">>>>>> 5. Idade invalida ==> ");
			
				//return modelAndView;
			}
			return modelAndView;
	}      /* else { //(Exception e) {
			modelAndView.setViewName("cadastrarCliente");
			//if (e.getMessage().contains("could not execute statement")) {
				modelAndView.addObject("message", "Dados invalidos - cliente já cadastrado.");
				logger.info(">>>>>> 5. cliente ja cadastrado ==> " );// e.getMessage());
			}
				modelAndView.addObject("message", "Erro não esperado - contate o administrador");
				logger.error(">>>>>> 5. erro nao esperado ==> " + e.getMessage());
			*/

	


	public Endereco obtemEndereco(String cep) {
		RestTemplate template = new RestTemplate();
		String url = "https://viacep.com.br/ws/{cep}/json/";
		Endereco endereco = template.getForObject(url, Endereco.class, cep);
		logger.info(">>>>>> 3. obtem endereco ==> " + endereco.toString());
		return endereco;
	}

	public String sendMail(Cliente cliente) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom("locacaodadelicia@gmail.com");
		message.setTo(cliente.getEmail());
		message.setSubject("Confirmação do cadastro de cliente");
		message.setText("Seu cadastro foi realizado - " + cliente.getNome() +"\n"
				+ "Endereço (Bairro): " + cliente.getEndereco().getBairro() +"\n"
						+ "Data de cadastro: " + cliente.getDataCadastro());
		try {
			mailSender.send(message); 
			logger.info(">>>>>> 5. Envio do e-mail processado com sucesso."); 
			return "Email enviado"; 
		} catch(Exception e) {
			 e.printStackTrace();
			 return "Erro ao enviar e-mail."; 
		}
		
		
	}

	//@Override
	//public ModelAndView saveOrUpdate(Cliente cliente) {
		// TODO Auto-generated method stub
		//return null;
	}

	
