package com.revature.nflfantasydraft.Controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.revature.nflfantasydraft.Entity.League;
import com.revature.nflfantasydraft.Service.LeagueService;
import jakarta.servlet.http.HttpServletRequest;

// @CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/leagues")
public class LeagueController {

    @Autowired
    LeagueService leagueService;

    @GetMapping("")
    public ResponseEntity<List<League>> getAllLeagues() {
        List<League> leagues; 

        leagues = leagueService.fetchAllLeagues();

        return new ResponseEntity<>(leagues, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getLeagueById(HttpServletRequest request, @PathVariable("id") Integer id) {
        System.out.println("INSIDE GET LEAGUE BY ID HERE");
        try {
            System.out.println("Before calling leagueService");
            League league = leagueService.fetchLeagueById(id);
            System.out.println("After calling leagueService");
            return new ResponseEntity<>(league, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("error", e.getMessage());
            return new ResponseEntity<>(errorMap, HttpStatus.INTERNAL_SERVER_ERROR); // 500 if it's truly an exception
        }
    }
    

    @PostMapping("")
    public ResponseEntity<Object> addLeague(HttpServletRequest request, @RequestBody Map<String, Object> leagueMap) {
        System.out.println("INSIDE ADD LEAGUE HERE");

        try {
            // --- get inputs ---
            Integer num_players = Integer.parseInt(leagueMap.get("numPlayers").toString());

            System.out.println("Before calling leagueService");
            // --- END: get inputs ---

            League league = leagueService.addLeague(num_players);

            System.out.println("After calling leagueService");
            return new ResponseEntity<>(league, HttpStatus.CREATED);
        } catch (Exception e) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("error", e.getMessage());
            return new ResponseEntity<>(errorMap, HttpStatus.INTERNAL_SERVER_ERROR); // 500 if it's truly an exception
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateLeague(HttpServletRequest request, @PathVariable("id") Integer id, @RequestBody League league) {
       try {
            leagueService.updateLeague(id, league);
            Map<String, Boolean> map = new HashMap<>();
            map.put("success", true);
            return new ResponseEntity<>(map, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("error", e.getMessage());
            return new ResponseEntity<>(errorMap, HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLeague(HttpServletRequest request, @PathVariable("id") Integer id) {
        try {
             leagueService.removeLeague(id);
             Map<String, Boolean> map = new HashMap<>();
             map.put("success", true);
             return new ResponseEntity<>(map, HttpStatus.OK);
         } catch (Exception e) {
             Map<String, String> errorMap = new HashMap<>();
             errorMap.put("error", e.getMessage());
             return new ResponseEntity<>(errorMap, HttpStatus.INTERNAL_SERVER_ERROR); // 500
         }
    }
    
}