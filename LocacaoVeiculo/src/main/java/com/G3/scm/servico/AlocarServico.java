package com.G3.scm.servico;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.autoconfigure.mail.MailSenderValidatorAutoConfiguration;

import com.G3.scm.model.Alocar;


public interface AlocarServico {
	public Iterable<Alocar> findAll();


	public Alocar findById(Long id);
	
	//public ModelAndView saveOrUpdate(Alocar alocacao);

	//public ModelAndView save(Alocar alocacao);
	
    //public ModelAndView saveOrUpdate2(Alocar alocacao);


}
