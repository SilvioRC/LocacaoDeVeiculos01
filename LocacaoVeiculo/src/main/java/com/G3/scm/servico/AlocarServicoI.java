package com.G3.scm.servico;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.autoconfigure.mail.MailSenderValidatorAutoConfiguration;

import com.G3.scm.model.Alocar;
import com.G3.scm.model.AlocarRepository;
import com.G3.scm.model.Cliente;
import com.G3.scm.model.Veiculo;
//import com.G3.scm.servico.ClienteServico;


@Service
public class AlocarServicoI implements AlocarServico {
	Logger logger = LogManager.getLogger(AlocarServicoI.class);
	@Autowired
	private AlocarRepository alocarRepository;
	@Autowired
	private ClienteServico servicoS;
	@Autowired
	private VeiculoServico servicoV;
	@Autowired
	private JavaMailSender mailSender;
	
	public Iterable<Alocar> findAll() {
		return alocarRepository.findAll();
	}
	
	public Alocar findById(Long id) {
		return alocarRepository.findById(id).get();
	}

	public ModelAndView save(Alocar alocacao) {
             ModelAndView modelAndView = new ModelAndView("consultarAlocacoes");
		
		Cliente cliente = servicoS.findByCpf(alocacao.getClienteCpf());
		Veiculo veiculo = servicoV.findByPlaca(alocacao.getVeiculoPlaca());
		if(cliente != null || veiculo != null) {
			alocacao.setDtInicio(alocacao.getDtInicioFormat());
			alocacao.setDtEntrega(alocacao.getDtEntregaFormat());
			int verifDataEntrega =  alocacao.getDtEntrega().getDayOfYear(); 
			int verifDataInicio = alocacao.getDtInicio().getDayOfYear();
			if(cliente.isAlocacao() == false && veiculo.isLocado() == false && verifDataEntrega >= verifDataInicio) {
				float valorT = (verifDataEntrega - verifDataInicio) * veiculo.getValorDiaria();
				alocacao.setValorTotal(valorT);
				alocacao.setSituacao(true);
				alocacao.setClienteNome(cliente.getNome());
				alocacao.setVeiculoNome(veiculo.getNome());
				cliente.setAlocacao(true);
				veiculo.setLocado(true);
				cliente.setDataCadastro(new DateTime());
				veiculo.setDataCadastro(new DateTime());
				alocarRepository.save(alocacao);
				logger.info(">>>>>> 4. comando save executado  ");
				sendMail(alocacao); 
				modelAndView.addObject("alocados", alocarRepository.findAll());
			}
			else {
				modelAndView.setViewName("alocar");
				if(verifDataInicio > verifDataEntrega) {
					modelAndView.addObject("message", "Data de Entrega invalida");
				}
				
				else {
					modelAndView.addObject("message", "Cliente ou veiculo já possui alguma alocação");
				}
				
			}
		}
		else {
			modelAndView.setViewName("alocar");
			modelAndView.addObject("message", "Cliente ou veiculo não existe");
		}
		return modelAndView;
	}
	
		/* ModelAndView modelAndView = new ModelAndView("consultarAlocacoes");
		
			Cliente cliente = servicoC.findByCpf(alocacao.getClienteCpf());
			Veiculo veiculo = servicoV.findByPlaca(alocacao.getVeiculoPlaca());
		     try {
		    	 
				
			alocacao.setClienteId(cliente.getId());
			alocacao.setClienteNome(cliente.getNome());
			alocacao.setVeiculoId(veiculo.getId());
			alocacao.setVeiculoNome(veiculo.getFabricante());
			alocacao.setDtInicio(alocacao.getDtInicioFormat());
			alocacao.setDtEntrega(alocacao.getDtEntregaFormat());
			float valorT = (alocacao.getDtEntrega().getDayOfMonth() - alocacao.getDtInicio().getDayOfMonth()) * veiculo.getValorDiaria();
			alocacao.setValorTotal(valorT);
			cliente.setAlocacao(true);
			cliente.setAlocacaoId(alocacao.getId());
			veiculo.setLocado(true);
			alocarRepository.save(alocacao);
			
			logger.info(">>>>>> 4. comando save executado  ");
			modelAndView.addObject("alocados", alocarRepository.findAll());
		     }
			
			 catch (Exception e) {
			modelAndView.setViewName("alocar");
			if (e.getMessage().contains("could not execute statement")) {
				modelAndView.addObject("message", "Dados invalidos - cliente já cadastrado.");
				logger.info(">>>>>> 5. cliente ja cadastrado ==> " + e.getMessage());
			} else {
				modelAndView.addObject("message", "Erro não esperado - contate o administrador");
				logger.error(">>>>>> 5. erro nao esperado ==> " + e.getMessage());
			}
		}
			
		return modelAndView;
	} */


		public ModelAndView save2(Alocar alocacao) {
			ModelAndView modelAndView = new ModelAndView("consultarAlocacoes");
			
			
			Cliente cliente = servicoS.findByCpf(alocacao.getClienteCpf());
			Veiculo veiculo = servicoV.findByPlaca(alocacao.getVeiculoPlaca());
			
			cliente.setAlocacao(false);
			veiculo.setLocado(false);
			cliente.setDataCadastro(new DateTime());
			veiculo.setDataCadastro(new DateTime());
			alocacao.setDtInicio(alocacao.getDtInicioFormat());
			alocacao.setDtEntrega(alocacao.getDtEntregaFormat());
			float valorT = (alocacao.getDtEntrega().getDayOfMonth() - alocacao.getDtInicio().getDayOfMonth()) * veiculo.getValorDiaria();
			alocacao.setValorTotal(valorT);
			alocarRepository.save(alocacao);
			logger.info(">>>>>> 4. comando save executado  ");
			modelAndView.addObject("alocados", alocarRepository.findAll());
				
			 
			return modelAndView;
		}
		public String sendMail(Alocar alocacao) {
			Cliente cliente = servicoS.findByCpf(alocacao.getClienteCpf());
			Veiculo veiculo = servicoV.findByPlaca(alocacao.getVeiculoPlaca());
			SimpleMailMessage message = new SimpleMailMessage();
			message.setFrom("locacaodadelicia@gmail.com");
			message.setTo(cliente.getEmail());
			message.setSubject("Confirmação do cadastro de cliente");
			message.setText("Olá " + cliente.getNome() +", reserva efetuada com sucesso"
					+ " do veiculo " + veiculo.getNome() + " Placa: " + veiculo.getPlaca());
			try {
				mailSender.send(message); 
				logger.info(">>>>>> 5. Envio do e-mail processado com sucesso."); 
				return "Email enviado"; 
			} catch(Exception e) {
				 e.printStackTrace();
				 return "Erro ao enviar e-mail."; 
			}
		}
}
			

		
		
		

		
	

	



