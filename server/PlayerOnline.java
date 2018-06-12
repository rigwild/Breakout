//Class for the server
public class PlayerOnline extends Player
{
   private String UDID;
   private String webSocketID;

   public PlayerOnline(String vpseudo, String vUDID)
   {
      UDID = vUDID;
      setPlayerPseudo(vpseudo);
   }

   public PlayerOnline(String vpseudo, String vUDID, String vwebSocketID)
   {
      UDID = vUDID;
      webSocketID = vwebSocketID;
      setPlayerPseudo(vpseudo);
   }

   public PlayerOnline(String vUDID)
   {
      UDID = vUDID;
   }

   public PlayerOnline(PlayerOnline p)
   {
      setPlayerId(p.getPlayerId());
      setPlayerPseudo(p.getPlayerPseudo());
      UDID = p.getUDID();
   }

   public String getUDID()
   {
      return UDID;
   }

   public String getWebSocketID()
   {
      return webSocketID;
   }

   public void setPlayerStartCoordinates(Coordinate c)
   {
      super.setPlayerStartCoordinate(c);
   }


   public boolean equals(PlayerOnline p)
   {
      return (p.UDID.equals(UDID) && p.getPlayerPseudo().equals(getPlayerPseudo()));
   }
}
