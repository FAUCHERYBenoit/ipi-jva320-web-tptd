package com.ipi.jva320.controllers;

import com.ipi.jva320.exception.SalarieException;
import com.ipi.jva320.model.SalarieAideADomicile;
import com.ipi.jva320.repository.SalarieAideADomicileRepository;
import com.ipi.jva320.service.SalarieAideADomicileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class SalarieController {
	
	@Autowired
	SalarieAideADomicileService salarieAideADomicileService;
	
	@Autowired
	private SalarieAideADomicileRepository salarieAideADomicileRepository;
	
	@GetMapping(value = "/salaries/{id}")
	public String salarieGet(@PathVariable String id, final ModelMap model) {
		SalarieAideADomicile salarie = salarieAideADomicileService.getSalarie(Long.valueOf(id));
		model.put("salarie",salarie);
		model.put("listCount", salarieAideADomicileService.countSalaries());
		return "detail_Salarie";
	}
	
	@GetMapping(value = "/salaries/aide/new")
	public String salarieInputSave(final ModelMap model) {
		SalarieAideADomicile salarie = new SalarieAideADomicile();
		model.put("salarie",salarie);
		model.put("listCount", salarieAideADomicileService.countSalaries());
		return "detail_Salarie";
	}
	
	@PostMapping(value = "/salaries/save")
	public String salarieSaveForm(@ModelAttribute SalarieAideADomicile salarieAideADomicile) throws SalarieException {
		if(salarieAideADomicile.getId() != null){
			salarieAideADomicileService.updateSalarieAideADomicile(salarieAideADomicile);
		}
		else{
			SalarieAideADomicile _salarieAideADomicile = salarieAideADomicileService.creerSalarieAideADomicile(salarieAideADomicile);
			return "redirect:/salaries/" + _salarieAideADomicile.getId();
		}
		return "redirect:/salaries/" + salarieAideADomicile.getId();
	}
	
	@GetMapping(value = "/salaries/{id}/delete")
	public String salarieDelete(@PathVariable String id, final ModelMap model) throws SalarieException {
		salarieAideADomicileService.deleteSalarieAideADomicile(Long.valueOf(id));
		return "redirect:/";
	}
	
	@GetMapping(value = "/salaries")
	public String salaries(@RequestParam String page, @RequestParam String size, @RequestParam String sortProperty, @RequestParam String sortDirection, final ModelMap model) {
		int currentPage = Integer.parseInt(page);
		int pageSize = Integer.parseInt(size);

		Page<SalarieAideADomicile> salaries = salarieAideADomicileService.getSalaries(
				PageRequest.of(currentPage, pageSize, Sort.by("id")));
		
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
		model.put("listCount", salarieAideADomicileService.countSalaries());
		model.put("salaries",salaries);
		return "list";
	}
}
