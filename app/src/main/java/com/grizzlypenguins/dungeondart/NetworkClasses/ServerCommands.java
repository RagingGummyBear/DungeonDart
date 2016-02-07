package com.grizzlypenguins.dungeondart.NetworkClasses;

/**
 * Created by Darko on 05.02.2016.
 */
public class ServerCommands {

    //Sending messages

    public static String onlineMapFinish = "IhaveDoneMyJob\n";
    public static String onlineMapGetMapWIthID = "GetMapWithID\n";
    public static String onlineMapGetAllMaps = "GetAllMaps\n";
    public static String onlineMapNewMap = "New map\n";
    public static String onlineMapNewMapFinish = "Finished Entering\n";
    public static String onlineMapNewMapName = "MapName: \n";

    //Receiving messages

    public static String rOnlineMapFinished = "Finished adding new map";
    public static String rOnlineMapStartRecieve = "SendingRequestedMap";
    public static String rOnlineMapEndRecieve = "DoneSendingRequestedMap";
    public static String rOnlineMapStartSendingAllMaps = "SendingAllMaps";
    public static String rOnlineMapEndSendingAllMaps = "AllMapsSent";
    public static String rOnlineMapNewMapStart = "Adding new map";
    public static String rOnlineMapNewMapEnd = "Finished adding new map";

    //From Activity to online class commands

    public static String cGetAllMaps = "RequestAllMaps";
    public static String cGetMapWithID = "GetMapWIthID";
    public static String cAddNewMap = "AddNewMapToServer";



}
