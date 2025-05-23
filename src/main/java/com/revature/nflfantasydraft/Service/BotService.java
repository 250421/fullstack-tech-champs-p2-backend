    package com.revature.nflfantasydraft.Service;

    import java.util.List;

import com.revature.nflfantasydraft.Dto.BotPickResponseDto;
import com.revature.nflfantasydraft.Dto.BotRequestDto;
    import com.revature.nflfantasydraft.Dto.BotResponseDto;
    import com.revature.nflfantasydraft.Dto.BotTeamRequestDto;
    import com.revature.nflfantasydraft.Dto.TeamResponseDto;
    import com.revature.nflfantasydraft.Entity.Bot;


    public interface BotService {
        BotResponseDto createBot(BotRequestDto botRequestDto);
        Bot updateBot(Bot bot);
        Bot getBotById(Long botId);
        TeamResponseDto createBotTeam(BotTeamRequestDto botTeamRequestDto);
        BotPickResponseDto botPickPlayer(Long teamId);
        List<TeamResponseDto> getAllBotTeams();
        List<BotResponseDto> getAllBots(); 


        void deleteBot(Long botId);
        void deleteBotTeam(Long teamId);
    }
 
  