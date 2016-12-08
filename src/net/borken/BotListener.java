package net.borken;

import net.borken.Outils.AntiSpam;
import net.borken.Outils.Moderateur;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.managers.GuildManager;


/**
 * Created by seb65 on 19/10/2016.
 */

public class BotListener extends ListenerAdapter {
    AntiSpam antispam=new AntiSpam();
    Moderateur modo = new Moderateur();
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        //                                                      ----------------------Test pour eviter eco de commande-------------------------
        try
        {
            if (event.getMessage().getContent().startsWith("//") && event.getMessage().getAuthor().getId() != event.getJDA().getSelfUser().getId()) {
                //On a detecter que c'etait une commande
                //System.out.println(event.getMessage().getContent());


                MainBot.handleCommand(MainBot.parser.parse(event.getMessage().getContent(), event));



            }
            else if (event.getMessage().getAuthor().getId() != event.getJDA().getSelfUser().getId() && !event.getTextChannel().getName().equals("le_dongeon"))
            {
                //N'est pas une commande

                Guild serveur=event.getGuild();
                GuildManager guildManager = serveur.getManager();
                Member user = event.getMember();

                // appel de la methode d'analyse de message de "Moderateur"
                if(!event.getAuthor().getName().equals("Aethex")) {
                    if (modo.analyse(user, serveur, guildManager, event) == 1) {
                        antispam.extermine(user, serveur, guildManager,true, event);
                    }
                }


            }
        }catch (Exception e)
        {
            if (e.getMessage()==null) {
                System.out.println(MainBot.entete.get("ERREUR", "BotListener") +"NullPointerException");
            } else {
                System.out.println(MainBot.entete.get("ERREUR", "BotListener") + e.getMessage());
            }
        }

    }
}
