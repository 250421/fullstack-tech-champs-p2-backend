    package com.revature.nflfantasydraft.Service;

    import java.util.List;

    import com.revature.nflfantasydraft.Dto.BotRequestDto;
    import com.revature.nflfantasydraft.Dto.BotResponseDto;
    import com.revature.nflfantasydraft.Dto.BotTeamRequestDto;
    import com.revature.nflfantasydraft.Dto.TeamResponseDto;


    public interface BotService {
        BotResponseDto createBot(BotRequestDto botRequestDto);
        TeamResponseDto createBotTeam(BotTeamRequestDto botTeamRequestDto);
        TeamResponseDto botPickPlayer(Long teamId);
        List<TeamResponseDto> getAllBotTeams();
        List<BotResponseDto> getAllBots(); 


        void deleteBot(Long botId);
        void deleteBotTeam(Long teamId);
    }
 
  