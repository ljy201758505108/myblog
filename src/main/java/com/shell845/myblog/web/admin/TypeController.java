package com.shell845.myblog.web.admin;

import com.shell845.myblog.po.Type;
import com.shell845.myblog.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

/**
 * @author ych
 * @date 16/4/2020 9:27 PM
 */

@Controller
@RequestMapping("/admin")
public class TypeController {
    @Autowired
    private TypeService typeService;

    @GetMapping("/types")
    public String types(@PageableDefault(size = 10,sort = {"id"},direction = Sort.Direction.DESC)
                                    Pageable pageable, Model model) {
        model.addAttribute("page", typeService.listType(pageable));
        return "admin/types";
    }

    // page of add new type
    @GetMapping("/types/input")
    public String input(Model model) {
        model.addAttribute("type", new Type());
        return "admin/input-type";
    }

    // get existing types
    @Transactional
    @GetMapping(value = "/types/{id}/input")
    public String editInput(@PathVariable Long id, Model model) {
        Type type = typeService.getType(id);
        model.addAttribute("type", type);
        System.out.println("------------" + type.toString());
        return "admin/input-type";
    }

    // add new type
    @PostMapping("/types")
    public String post(@Valid Type type, BindingResult result, RedirectAttributes attributes) {
        Type type1 = typeService.getTypeByName(type.getName());

        if (type1 != null) {
            result.rejectValue("name","nameError","Type already exists");
        }
        if (result.hasErrors()) {
            return "admin/input-type";
        }

        Type t = typeService.saveType(type);
        if (t == null) {
            attributes.addFlashAttribute("message", "Add fail");
        } else {
            attributes.addFlashAttribute("message", "Add complete");
        }
        return "redirect:/admin/types";
    }

    // edit existing type
    @Transactional
    @PostMapping("/types/{id}")
    public String editPost(@Valid Type type, BindingResult result,@PathVariable Long id, RedirectAttributes attributes) {
        Type type1 = typeService.getTypeByName(type.getName());
        if (type1 != null) {
            result.rejectValue("name","nameError","Type already exists");
        }
        if (result.hasErrors()) {
            return "admin/input-type";
        }
        Type t = typeService.updateType(id,type);
        if (t == null ) {
            attributes.addFlashAttribute("message", "Update fail");
        } else {
            attributes.addFlashAttribute("message", "Update success");
        }
        return "redirect:/admin/types";
    }

    // delete type
    @GetMapping("/types/{id}/delete")
    public String delete(@PathVariable Long id,RedirectAttributes attributes) {
        typeService.deleteType(id);
        attributes.addFlashAttribute("message", "Deleted");
        return "redirect:/admin/types";
    }
}
