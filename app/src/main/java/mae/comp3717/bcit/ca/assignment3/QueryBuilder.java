package mae.comp3717.bcit.ca.assignment3;


public class QueryBuilder {

    /**
     * Specify your database name here
     * @return
     */
    public String getDatabaseName() {
        return "asn3-students";
    }

    /**
     * Specify your MongoLab API here
     * @return
     */
    public String getApiKey() {
        return "V1O8PT16x-6LcZMXTnREH87LNi1m0pwC";
    }

    /**
     * This constructs the URL that allows you to manage your database,
     * collections and documents
     * @return
     */
    public String getBaseUrl()
    {
        return "https://api.mongolab.com/api/1/databases/"+getDatabaseName()+"/collections/";
    }

    /**
     * Completes the formatting of your URL and adds your API key at the end
     * @return
     */
    public String docApiKeyUrl()
    {
        return "?apiKey="+getApiKey();
    }

    /**
     * Returns the students collection
     * @return
     */
    public String documentRequest()
    {
        return "students";
    }

    /**
     * Builds a complete URL using the methods specified above
     * @return
     */
    public String buildContactsSaveURL()
    {
        return getBaseUrl()+documentRequest()+docApiKeyUrl();
    }

    /**
     * Formats the contact details for MongoHQ Posting
     * @param contact: Details of the person
     * @return
     */

}
