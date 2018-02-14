package net.Broken.Commands;

import net.Broken.Commande;
import net.Broken.MainBot;
import net.Broken.Tools.EmbedMessageUtils;
import net.Broken.Tools.MessageTimeOut;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.HierarchyException;
import net.dv8tion.jda.core.managers.GuildController;
import net.dv8tion.jda.core.managers.GuildManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by seb65 on 20/10/2016.
 */

/**
 *
 */
public class Move implements Commande {

    Logger logger = LogManager.getLogger();
    private String HELP="`//move <@utilisateur> <@rôleCible>`\n:arrow_right:\t*Deplacement d'un utilisateur vers un rôle cible, attention à bien faire des montions.*";
    public List<Role> saveRoleUser;
    public Member user;
    public Guild serveur;
    public GuildManager serveurManager;
    public GuildController guildController;

    /**
     *
     * @param user
     * @param cible
     * @param reset
     * @param serveur
     * @param serveurManager
     * @return
     */
    public boolean exc(Member user, List<Role> cible , boolean reset, Guild serveur, GuildManager serveurManager) throws HierarchyException
    {
        MainBot.roleFlag = true;
        guildController = new GuildController(serveur);
        boolean erreur = false;
        List<Role> allRoll = serveur.getRoles();



        //On recupere les roles de l'utilisateur

        List<Role> roleUserList = user.getRoles();

        logger.info("Roles de " + user.getEffectiveName() + ":");

        //On les save
        saveRoleUser = roleUserList;

        //Ajout du role cible

        //On transforme la le role a ajouter en une liste pour pouvoir l'utiliser dans modifyMemberRoles


        //on fait ensuite les modif
        guildController.modifyMemberRoles(user,cible).complete();

        logger.info("Role " + cible + " attribuer a " + user.getEffectiveName());

        this.user=user;
        this.serveur=serveur;
        this.serveurManager=serveurManager;
        return erreur;
    }

    @Override

    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    /**
     *
     * @param args
     * @param event
     */
    public void action(String[] args, MessageReceivedEvent event)
    {
        if(!event.isFromType(ChannelType.PRIVATE))
        {
            if(args.length>=2)
            {
                serveur=event.getGuild();
                List<User> userL = event.getMessage().getMentionedUsers();
                List<Role> roleL = event.getMessage().getMentionedRoles();

                if(userL.size()<1 ||roleL.size()<1)
                {
                    logger.warn("Mentionnement Incorect.");
                    Message rest = event.getTextChannel().sendMessage(EmbedMessageUtils.getMoveError("Utilisateur ou Role mal mentioner.")).complete();
                    List<Message> messages = new ArrayList<Message>(){{
                        add(rest);
                        add(event.getMessage());
                    }};
                    new MessageTimeOut(messages,MainBot.messageTimeOut).start();
                }
                else
                {
                    user = serveur.getMember(userL.get(0));
                    serveur=event.getGuild();
                    logger.info("Tentative de déplacement de "+user.getEffectiveName()+" vers "+roleL+" par l'utilisateur "+event.getAuthor().getName());
                    if(event.getMember().hasPermission(Permission.ADMINISTRATOR))
                    {

                        logger.info("Autorisation suffisante, deplacement autorisé");
                        logger.debug("Utilisateur trouvée");
                        try {
                            boolean erreur=this.exc(user,roleL,true,serveur,serveur.getManager());
                            if(erreur)
                            {
                                Message rest = event.getTextChannel().sendMessage(EmbedMessageUtils.getMoveError("Verifier le rôle cible. ")).complete();
                                List<Message> messages = new ArrayList<Message>(){{
                                    add(rest);
                                    add(event.getMessage());
                                }};
                                new MessageTimeOut(messages,MainBot.messageTimeOut).start();
                            }
                            else
                            {
                                StringBuilder roleStr = new StringBuilder("");
                                boolean first = true;
                                for( Role role : roleL)
                                {
                                    if (!first) {
                                        roleStr.append(", ");

                                    }
                                    else
                                        first = false;
                                    roleStr.append("__");
                                    roleStr.append(role.getName());
                                    roleStr.append("__");
                                }


                                Message rest = event.getTextChannel().sendMessage(EmbedMessageUtils.getMoveOk("Déplacement de "+user.getEffectiveName()+" vers "+roleStr.toString()+" reussi.")).complete();
                                List<Message> messages = new ArrayList<Message>(){{
                                    add(rest);
                                    add(event.getMessage());
                                }};
                                new MessageTimeOut(messages,MainBot.messageTimeOut).start();
                            }
                        }catch (HierarchyException e){
                            Message rest = event.getTextChannel().sendMessage(EmbedMessageUtils.getMoveError("Impossible de déplacer un "+user.getRoles().get(0).getAsMention())).complete();
                            List<Message> messages = new ArrayList<Message>(){{
                                add(rest);
                                add(event.getMessage());
                            }};
                            new MessageTimeOut(messages,MainBot.messageTimeOut).start();
                            logger.error("Hierarchy error, please move bot's role on top!");
                        }


                    }
                    else
                    {
                        logger.warn("Autorisation insuffisante, deplacement refusé");
                        Message rest = event.getTextChannel().sendMessage(EmbedMessageUtils.getMoveError("Vous n'avez pas l'autorisation de faicre ça!")).complete();
                        List<Message> messages = new ArrayList<Message>(){{
                            add(rest);
                            add(event.getMessage());
                        }};
                        new MessageTimeOut(messages,MainBot.messageTimeOut).start();

                    }
                }

            }
            else
            {
                logger.warn("Arguments maquant.");
                Message rest = event.getTextChannel().sendMessage(EmbedMessageUtils.getMoveError("Arguments manquant.")).complete();
                List<Message> messages = new ArrayList<Message>(){{
                    add(rest);
                    add(event.getMessage());
                }};
                new MessageTimeOut(messages,MainBot.messageTimeOut).start();
            }
        }
        else
            event.getPrivateChannel().sendMessage(EmbedMessageUtils.getNoPrivate());








    }

    /**
     *
     * @param args
     * @return
     */
    @Override
    public String help(String[] args) {
        return HELP;
    }

    /**
     *
     * @param success
     * @param event
     */
    @Override
    public void executed(boolean success, MessageReceivedEvent event) {

    }

    @Override
    public boolean isPrivateUsable() {
        return false;
    }
}