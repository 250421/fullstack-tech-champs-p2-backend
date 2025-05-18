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

import com.revature.nflfantasydraft.Entity.DraftPick;
import com.revature.nflfantasydraft.Entity.Team;
import com.revature.nflfantasydraft.Service.DraftPickService;
import jakarta.servlet.http.HttpServletRequest;

// @CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/draft_picks")
public class DraftPickController {

    @Autowired
    DraftPickService draftPickService;

    @GetMapping("")
    public ResponseEntity<List<DraftPick>> getAllDraftPicks() {
        List<DraftPick> draftPicks; 

        draftPicks = draftPickService.fetchAllDraftPicks();

        return new ResponseEntity<>(draftPicks, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getDraftPickById(HttpServletRequest request, @PathVariable("id") Integer id) {
        System.out.println("INSIDE GET DRAFT PICK BY ID HERE");
        try {
            System.out.println("Before calling draftPickService");
            DraftPick draftPick = draftPickService.fetchDraftPickById(id);
            System.out.println("After calling draftPickService");
            return new ResponseEntity<>(draftPick, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("error", e.getMessage());
            return new ResponseEntity<>(errorMap, HttpStatus.INTERNAL_SERVER_ERROR); // 500 if it's truly an exception
        }
    }

    @GetMapping("/pick-number/{pick_number}")
    public ResponseEntity<Object> getDraftPickByPickNumber(HttpServletRequest request, @PathVariable("pick_number") Integer pick_number) {
        System.out.println("INSIDE GET DRAFT PICK BY PICK NUMBER HERE");
        try {
            System.out.println("Before calling draftPickService");
            DraftPick draftPick = draftPickService.fetchDraftPickByPickNumber(pick_number);
            System.out.println("After calling draftPickService");
            return new ResponseEntity<>(draftPick, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("error", e.getMessage());
            return new ResponseEntity<>(errorMap, HttpStatus.INTERNAL_SERVER_ERROR); // 500 if it's truly an exception
        }
    }
    

    @PostMapping("")
    public ResponseEntity<Object> addDraftPick(HttpServletRequest request, @RequestBody Map<String, Object> draftPickMap) {
        System.out.println("INSIDE ADD DRAFT PICK HERE");

        try {
            Integer league_id = Integer.parseInt(draftPickMap.get("leagueId").toString());
            Integer pick_number = Integer.parseInt(draftPickMap.get("pickNumber").toString());
            Integer team_id = Integer.parseInt(draftPickMap.get("teamId").toString());

            System.out.println("Before calling draftPickService");
            DraftPick draftPick = draftPickService.addDraftPick(league_id, pick_number, team_id);

            System.out.println("After calling draftPickService");
            return new ResponseEntity<>(draftPick, HttpStatus.CREATED);
        } catch (Exception e) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("error", e.getMessage());
            return new ResponseEntity<>(errorMap, HttpStatus.INTERNAL_SERVER_ERROR); // 500 if it's truly an exception
        }
    }

    @PutMapping("/pick-number/{pick_number}")
    public ResponseEntity<?> updateDraftPick(HttpServletRequest request, @PathVariable("pick_number") Integer pick_number, @RequestBody DraftPick draftPick) {
       try {
            draftPickService.updateDraftPick(pick_number, draftPick);
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
    public ResponseEntity<?> deleteDraftPick(HttpServletRequest request, @PathVariable("id") Integer id) {
        try {
             draftPickService.removeDraftPick(id);
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