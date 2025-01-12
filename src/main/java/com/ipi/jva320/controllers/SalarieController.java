package com.ipi.jva320.controllers;

import com.ipi.jva320.exception.SalarieException;
import com.ipi.jva320.model.SalarieAideADomicile;
import com.ipi.jva320.repository.SalarieAideADomicileRepository;
import com.ipi.jva320.service.SalarieAideADomicileService;
import org.attoparser.ParseException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.exceptions.TemplateInputException;

import java.util.*;

@Controller
public class SalarieController {
	
	@Autowired
	SalarieAideADomicileService salarieAideADomicileService;
	
	@Autowired
	private SalarieAideADomicileRepository salarieAideADomicileRepository;
	
	static boolean error = false;
	
	@GetMapping(value = { "/salaries/{id}", "/salaries/aide/{id}" })
	public String salarieGet(@PathVariable String id, final ModelMap model) {
		try{
			SalarieAideADomicile salarie = salarieAideADomicileService.getSalarie(Long.valueOf(id));
			if(salarie.getId() == null){
				throw new NullPointerException("No salarie found");
			}
			model.put("salarie",salarie);
			model.put("listCount", salarieAideADomicileService.countSalaries());
			model.put("title","Gestion des salariés");
			model.put("error",error);
		}
		catch (NullPointerException e){
			return "redirect:/";
		}
		
		return "detail_Salarie";
	}
	
	@GetMapping(value = "/salaries/aide/new")
	public String salarieInputSave(final ModelMap model) {
		SalarieAideADomicile salarie = new SalarieAideADomicile();
		model.put("salarie",salarie);
		model.put("title","Gestion des salariés");
		model.put("listCount", salarieAideADomicileService.countSalaries());
		model.put("error",error);
		return "detail_Salarie";
	}
	
	@PostMapping(value = "/salaries/save")
	public String salarieSaveForm(@ModelAttribute SalarieAideADomicile salarieAideADomicile) throws SalarieException {
		if(salarieAideADomicile.getId() != null){
			try{
				if(salarieAideADomicile.getNom().isEmpty()){
					throw new DataIntegrityViolationException("Empty name");
				}
				salarieAideADomicileService.updateSalarieAideADomicile(salarieAideADomicile);
				error = false;
			}
			catch (DataIntegrityViolationException e){
				error = true;
			}
		}
		else{
			try{
				if(salarieAideADomicile.getNom().isEmpty()){
					throw new DataIntegrityViolationException("Empty name");
				}
				SalarieAideADomicile _salarieAideADomicile = salarieAideADomicileService.creerSalarieAideADomicile(salarieAideADomicile);
				error = false;
				return "redirect:/salaries/" + _salarieAideADomicile.getId();
			}
			catch (DataIntegrityViolationException e){
				error = true;
				return "redirect:/salaries/aide/new";
			}
		}
		return "redirect:/salaries/" + salarieAideADomicile.getId();
	}
	
	@GetMapping(value = "/salaries/{id}/delete")
	public String salarieDelete(@PathVariable String id, final ModelMap model) throws SalarieException {
		salarieAideADomicileService.deleteSalarieAideADomicile(Long.valueOf(id));
		return "redirect:/";
	}
	
	@GetMapping(value = "/salaries")
	public String salaries(
			@RequestParam(required = false) String page,
			@RequestParam(required = false) String size,
			@RequestParam(required = false) String sortProperty,
			@RequestParam(required = false) String sortDirection,
			@RequestParam(required = false) String nom,
			final ModelMap model) {
		int currentPage;
		int pageSize;
		List<SalarieAideADomicile> listSalaries = Collections.emptyList();
		Page<SalarieAideADomicile> salaries = new PageImpl<>(listSalaries);
		error = false;
		
		if(page != null){
			currentPage = Integer.parseInt(page);
		}
		else{
			currentPage = 0;
		}
		if(size != null){
			pageSize = Integer.parseInt(size);
		}
		else{
			pageSize = 10;
		}
		if(nom != null){
			listSalaries = salarieAideADomicileService.getSalaries(nom, PageRequest.of(currentPage, pageSize, Sort.by("id")));
		}
		else{
			salaries = salarieAideADomicileService.getSalaries(
					PageRequest.of(currentPage, pageSize, Sort.by("id")));
		}
		
		
		if(Objects.equals(sortProperty, "id")){
			if (Objects.equals(sortDirection, "ASC")) {
				salaries = salarieAideADomicileService.getSalaries(
						PageRequest.of(currentPage, pageSize, Sort.by("id").ascending())
				);
			} else {
				salaries = salarieAideADomicileService.getSalaries(
						PageRequest.of(currentPage, pageSize, Sort.by("id").descending())
				);
			}
		}
		else if(Objects.equals(sortProperty, "nom")){
			if (Objects.equals(sortDirection, "ASC")) {
				salaries = salarieAideADomicileService.getSalaries(
						PageRequest.of(currentPage, pageSize, Sort.by("nom").ascending())
				);
			} else {
				salaries = salarieAideADomicileService.getSalaries(
						PageRequest.of(currentPage, pageSize, Sort.by("nom").descending())
				);
			}
		}
		model.put("currentPage", currentPage);
		model.put("maxPage", Math.ceil((float) salarieAideADomicileService.countSalaries()/pageSize));
		model.put("listCount", salarieAideADomicileService.countSalaries());
		if(nom != null){
			model.put("salaries",listSalaries);
		}
		else{
			model.put("salaries",salaries);
		}
		model.put("title","Gestion des salariés");
		model.put("error",error);
		return "list";
	}
}
