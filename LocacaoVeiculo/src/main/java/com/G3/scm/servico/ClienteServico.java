package com.G3.scm.servico;

import org.springframework.web.servlet.ModelAndView;
import com.G3.scm.model.Cliente;
import com.G3.scm.model.Endereco;
import com.G3.scm.servico.AlocarServicoI;
import com.G3.scm.servico.ClienteServico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.autoconfigure.mail.MailSenderValidatorAutoConfiguration;

public interface ClienteServico {
	public Iterable<Cliente> findAll();

	public Cliente findByCpf(String cpf);

	public void deleteById(Long id);

	public Cliente findById(Long id);

	//public ModelAndView saveOrUpdate(Cliente cliente);

	public Endereco obtemEndereco(String cep);
	
	public String sendMail(Cliente cliente);
}