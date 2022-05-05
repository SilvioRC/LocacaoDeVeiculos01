package com.G3.scm.controller;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
//import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.ModelAndView;

import com.G3.scm.model.Cliente;
//import com.G3.scm.model.Veiculo;
import com.G3.scm.servico.ClienteServicoI;
import com.G3.scm.servico.VeiculoServico;

@Controller
@RequestMapping(path = "/sig")
public class ClienteController {
	Logger logger = LogManager.getLogger(ClienteController.class);
	@Autowired
	ClienteServicoI servico;
	@Autowired
	VeiculoServico servicoV;

	@GetMapping("/menuCliente")
	public ModelAndView menuCliente() {
		return new ModelAndView("menuCliente");
	}

	@GetMapping("/clientes")
	public ModelAndView retornaFormDeConsultaTodosClientes() {
		ModelAndView modelAndView = new ModelAndView("consultarCliente");
		modelAndView.addObject("clientes", servico.findAll());
		return modelAndView;
	}

	@GetMapping("/cliente")
	public ModelAndView retornaFormDeCadastroDe(Cliente cliente) {
		ModelAndView mv = new ModelAndView("cadastrarCliente");
		mv.addObject("cliente", cliente);
		return mv;
	}

	@GetMapping("/clientes/{cpf}") // diz ao metodo que ira responder a uma requisicao do tipo get
	public ModelAndView retornaFormParaEditarCliente(@PathVariable("cpf") String cpf) {
		ModelAndView modelAndView = new ModelAndView("atualizarCliente");
		modelAndView.addObject("cliente", servico.findByCpf(cpf)); // o repositorio e injetado no controller
		return modelAndView; // addObject adiciona objetos para view
	}

	@GetMapping("/cliente/{id}")
	public ModelAndView excluirNoFormDeConsultaCliente(@PathVariable("id") Long id) {
		ModelAndView modelAndView = new ModelAndView("consultarCliente");
		Cliente cliente = servico.findById(id);
		if (cliente.isAlocacao() == true) {
			modelAndView.addObject("message", "Cliente possui uma alocação, impossivel de excluir nesse momento.");
		} else {
			servico.deleteById(id);
			logger.info(">>>>>> 1. servico de exclusao chamado para o id => " + id);
		}
		modelAndView.addObject("clientes", servico.findAll());
		return modelAndView;
	}

	@PostMapping("/clientes")
	public ModelAndView save(@Valid Cliente cliente, BindingResult result) {
		ModelAndView modelAndView = new ModelAndView("consultarCliente");
		if (result.hasErrors()) {
			modelAndView.setViewName("cadastrarCliente");
		} else {
			try {
				modelAndView = servico.save(cliente);
			} catch (HttpClientErrorException e) {
				modelAndView.addObject("message", "Dados invalidos - 400 Bad Request");
				modelAndView.setViewName("cadastrarCliente");
			} catch (ConstraintViolationException e) {
				modelAndView.addObject("message", "Dados invalidos - constraint violation");
				modelAndView.setViewName("cadastrarCliente");
			} catch (DataIntegrityViolationException e) {
				modelAndView.addObject("message", "Dados invalidos - cliente já cadastrado");
				modelAndView.setViewName("cadastrarCliente");
			} catch (Exception e) {
				modelAndView = new ModelAndView("cadastrarCliente");
				modelAndView.addObject("message", "Erro não esperado - contate o administrador ==>" + e.getMessage());
				logger.error(">>>>> 5. erro nao esperado ==> " + e.getMessage());
				logger.error(">>>>> 5. erro nao esperado ==> " + e.toString());
			}

		}
		return modelAndView;
	}

	@PostMapping("/clientes/{id}")
	public ModelAndView atualizaCliente(@PathVariable("id") Long id, @Valid Cliente cliente, BindingResult result) {
		ModelAndView modelAndView = new ModelAndView("consultarCliente");
		if (result.hasErrors()) {
			cliente.setId(id);
			return new ModelAndView("atualizarCliente");
		}
		// programacao defensiva - deve-se verificar se o Cliente existe antes de atualizar
				Cliente umCliente = servico.findById(id);
				umCliente.setCpf(cliente.getCpf());
				umCliente.setNome(cliente.getNome());
				umCliente.setDtNascimentoFormat(cliente.getDtNascimentoFormat());
				umCliente.setDtNascimento(cliente.getDtNascimentoFormat());
				umCliente.setTelefone(cliente.getTelefone());
				umCliente.setEmail(cliente.getEmail());
				umCliente.setCep(cliente.getCep());
				modelAndView = servico.save(umCliente);
				return modelAndView;
	}
}
	/*Logger logger = LogManager.getLogger(ClienteController.class);
	@Autowired
	ClienteServicoI servico;
	@Autowired
	VeiculoServico servicoV;

	@GetMapping("/menuCliente")
	public ModelAndView menuCliente() {
		return new ModelAndView("menuCliente");
	}

	@GetMapping("/clientes")
	public ModelAndView retornaFormDeConsultaTodosClientes() {
		ModelAndView modelAndView = new ModelAndView("consultarCliente");
		modelAndView.addObject("clientes", servico.findAll());
		return modelAndView;
	}

	@GetMapping("/cliente")
	public ModelAndView retornaFormDeCadastroDe(Cliente cliente) {
		ModelAndView mv = new ModelAndView("cadastrarCliente");
		mv.addObject("cliente", cliente);
		return mv;
	}

	@GetMapping("/clientes/{cpf}") // diz ao metodo que ira responder a uma requisicao do tipo get
	public ModelAndView retornaFormParaEditarCliente(@PathVariable("cpf") String cpf) {
		ModelAndView modelAndView = new ModelAndView("atualizarCliente");
		modelAndView.addObject("cliente", servico.findByCpf(cpf)); // o repositorio e injetado no controller
		return modelAndView; // addObject adiciona objetos para view
	}

	@GetMapping("/cliente/{id}")
	public ModelAndView excluirNoFormDeConsultaCliente(@PathVariable("id") Long id) {
		servico.deleteById(id);
		logger.info(">>>>>> 1. servico de exclusao chamado para o id => " + id);
		ModelAndView modelAndView = new ModelAndView("consultarCliente");
		modelAndView.addObject("clientes", servico.findAll());
		return modelAndView;
	}

	@PostMapping("/clientes")
	public void save(@Valid Cliente cliente, BindingResult result) {
		ModelAndView modelAndView = new ModelAndView("consultarCliente");
		if (result.hasErrors()) {
			modelAndView.setViewName("cadastrarCliente");
		} else {
			try {
				servico.save(cliente);
				modelAndView.addObject("clientes", servico.findAll());
				logger.error(">>>>>> 5. controller save chamada para consultar clientes ");
			} catch (HttpClientErrorException e) {
				modelAndView.addObject("message", "Dados invalidos - 400 Bad Request");
				modelAndView.setViewName("cadastrarCliente");
			} catch (ConstraintViolationException e) {
				modelAndView.addObject("message", "Dados invalidos - constraint violation");
				modelAndView.setViewName("cadastrarCliente");
			} catch (DataIntegrityViolationException e) {
				modelAndView.addObject("message", "Dados invalidos - cliente já cadastrado");
				modelAndView.setViewName("cadastrarCliente");
			} catch (Exception e) {
				modelAndView = new ModelAndView("cadastrarCliente");
				modelAndView.addObject("message", "Erro não esperado - contate o administrador ==>" + e.getMessage());
				logger.error(">>>>> 5. erro nao esperado ==> " + e.getMessage());
				logger.error(">>>>> 5. erro nao esperado ==> " + e.toString());
			}

		}
		modelAndView = servico.saveOrUpdate(cliente);
	}  

	

	@PostMapping("/clientes/{id}")
	public ModelAndView atualizaCliente(@PathVariable("id") Long id, @Valid Cliente cliente, BindingResult result) {
		ModelAndView modelAndView = new ModelAndView("consultarCliente");
		if (result.hasErrors()) {
			cliente.setId(id);
			return new ModelAndView("atualizarCliente");
		}
// programacao defensiva - deve-se verificar se o Cliente existe antes de atualizar
		Cliente umCliente = servico.findById(id);
		umCliente.setCpf(cliente.getCpf());
		umCliente.setNome(cliente.getNome());
		umCliente.setDtNascimentoFormat(cliente.getDtNascimentoFormat());
		umCliente.setDtNascimento(cliente.getDtNascimentoFormat());
		umCliente.setTelefone(cliente.getTelefone());
		umCliente.setEmail(cliente.getEmail());
		umCliente.setCep(cliente.getCep());
		DateTime dataAtual = new DateTime();
		DateTime ("setDataUltimaTransacao" , (dataAtual));
		modelAndView = servico.saveOrUpdate(umCliente);
		umCliente.setEmail(cliente.getEmail());

		return modelAndView;
	}


	private void DateTime(String string, DateTime dateTime) {
		// TODO Auto-generated method stub
		
	}
	
	*/

	/*
	 * @GetMapping("/alocar") public ModelAndView alocacao(Cliente cliente) {
	 * ModelAndView mv = new ModelAndView("alocar"); mv.addObject("cliente",
	 * cliente); return mv; }
	 * 
	 * @PostMapping("/realizarAlocacao") public ModelAndView alocarCliente( Cliente
	 * cliente) { ModelAndView modelAndView = new ModelAndView("consultarCliente");
	 * Cliente umCliente = servico.findByCpf(cliente.getCpf()); Veiculo umVeiculo =
	 * servicoV.findByPlaca(cliente.getVeiculoPlaca()); if(umVeiculo.isLocado() ==
	 * true || umCliente.isAlocacao() == true ) { return new ModelAndView("alocar");
	 * }
	 * 
	 * else { umCliente.setVeiculoPlaca(umVeiculo.getPlaca());
	 * umCliente.setVeiculoNome(umVeiculo.getNome()); umCliente.setAlocacao(true);
	 * umVeiculo.setLocado(true); modelAndView = servico.saveOrUpdate(umCliente);
	 * return modelAndView; }
	 * 
	 * }
	 * 
	 * @GetMapping("/desalocar") public ModelAndView desalocacao(Cliente cliente) {
	 * ModelAndView mv = new ModelAndView("desalocar"); mv.addObject("cliente",
	 * cliente); return mv; }
	 * 
	 * @PostMapping("/realizarDesalocacao") public ModelAndView DesalocarCliente(
	 * Cliente cliente) { ModelAndView modelAndView = new
	 * ModelAndView("consultarCliente"); Cliente umCliente =
	 * servico.findByCpf(cliente.getCpf()); Veiculo umVeiculo =
	 * servicoV.findByPlaca(umCliente.getVeiculoPlaca()); if(umVeiculo.isLocado() ==
	 * false || umCliente.isAlocacao() == false ) { return new
	 * ModelAndView("desalocar"); }
	 * 
	 * else { umCliente.setVeiculoPlaca(""); umCliente.setVeiculoNome("");
	 * umCliente.setAlocacao(false); umVeiculo.setLocado(false); modelAndView =
	 * servico.saveOrUpdate(umCliente); return modelAndView; }
	 * 
	 * }
	 */


