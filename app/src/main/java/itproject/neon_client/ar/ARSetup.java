package itproject.neon_client.ar;

import eu.kudan.kudan.ARAPIKey;


public class ARSetup
{
    public static void setupAR(String readKey)
    {
        ARAPIKey key = ARAPIKey.getInstance();
        key.setAPIKey(readKey);
    }
}
