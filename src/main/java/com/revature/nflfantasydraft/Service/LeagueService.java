package com.revature.nflfantasydraft.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.revature.nflfantasydraft.Entity.League;
import com.revature.nflfantasydraft.Exceptions.EtBadRequestException;
import com.revature.nflfantasydraft.Exceptions.EtResourceNotFoundException;
import com.revature.nflfantasydraft.Repository.LeagueRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class LeagueService {

    @Autowired
    LeagueRepository leagueRepository;

    public List<League> fetchAllLeagues() {
        return leagueRepository.findAll();
    }

    public League fetchLeagueById(Integer id) throws EtResourceNotFoundException {
        return leagueRepository.findById(id);
    }

    public League addLeague(Integer num_players) throws EtBadRequestException {
        int leagueId = leagueRepository.create(num_players);
        System.out.println("Returned league ID service: " + leagueId);
        return leagueRepository.findById(leagueId);
    }

    public void updateLeague(Integer leagueId, League league) throws EtBadRequestException {
        leagueRepository.update(leagueId, league);
    }

    public void removeLeague(Integer leagueId) throws EtResourceNotFoundException {
        leagueRepository.removeById(leagueId);
    }
    
}