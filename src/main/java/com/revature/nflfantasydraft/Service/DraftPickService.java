package com.revature.nflfantasydraft.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.revature.nflfantasydraft.Entity.DraftPick;
import com.revature.nflfantasydraft.Exceptions.EtBadRequestException;
import com.revature.nflfantasydraft.Exceptions.EtResourceNotFoundException;
import com.revature.nflfantasydraft.Repository.DraftPickRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class DraftPickService {

    @Autowired
    DraftPickRepository draftPickRepository;

    public List<DraftPick> fetchAllDraftPicks() {
        return draftPickRepository.findAll();
    }

    public DraftPick fetchDraftPickById(Integer id) throws EtResourceNotFoundException {
        return draftPickRepository.findById(id);
    }

    public DraftPick fetchDraftPickByPickNumber(Integer pick_number) throws EtResourceNotFoundException {
        return draftPickRepository.findByPickNumber(pick_number);
    }

    public DraftPick addDraftPick(Integer league_id, Integer pick_number, Integer team_id) throws EtBadRequestException {
        int draftPickId = draftPickRepository.create(league_id, pick_number, team_id);
        System.out.println("Returned DRAFT PICK ID service: " + draftPickId);
        return draftPickRepository.findById(draftPickId);
    }

    public void updateDraftPick(Integer pickNumber, DraftPick draftPick) throws EtBadRequestException {
        draftPickRepository.update(pickNumber, draftPick);
    }

    public void removeDraftPick(Integer draftPickId) throws EtResourceNotFoundException {
        draftPickRepository.removeById(draftPickId);
    }
    
}