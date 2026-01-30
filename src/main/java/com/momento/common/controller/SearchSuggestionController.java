package com.momento.common.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.momento.event.model.EventRepository;
import com.momento.prod.model.ProdRepository;

@RestController
@RequestMapping("/api/search-suggestion")
public class SearchSuggestionController {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ProdRepository prodRepository;

    @GetMapping("/popular")
    public List<String> getPopularSuggestions() {
        List<String> suggestions = new ArrayList<>();

        // Fetch random event titles and random product names
        List<String> eventTitles = eventRepository.findRandomTitles();
        List<String> prodNames = prodRepository.findRandomNames();

        if (eventTitles != null)
            suggestions.addAll(eventTitles);
        if (prodNames != null)
            suggestions.addAll(prodNames);

        // Shuffle for mixed randomness
        java.util.Collections.shuffle(suggestions);

        // Return only top 4-5
        return suggestions.size() > 5 ? suggestions.subList(0, 5) : suggestions;
    }
}
