package edu.ucalgary.ensf409;

/**
 *
 */
public class Lamp
{
    private String id;
    private String type;
    private String base;
    private String bulb;
    private int price;
    private String manuId;

    /**
     *
     * @param id
     * @param type
     * @param base
     * @param bulb
     * @param price
     * @param manuId
     */
    public Lamp(String id, String type, String base, String bulb, int price, String manuId){
        this.id = id;
        this.type = type;
        this.base = base;
        this.bulb = bulb;
        this.price = price;
        this.manuId = manuId;
    }

    /**
     *
     * @return
     */
    public String getId() { return this.id; }

    /**
     *
     * @return
     */
    public String getType() {
        return this.type;
    }

    /**
     *
     * @return
     */
    public String getBase() {
        return this.base;
    }

    /**
     *
     * @return
     */
    public String getBulb()
    {
        return this.bulb;
    }

    /**
     *
     * @return
     */
    public int getPrice()
    {
        return this.price;
    }

    /**
     *
     * @return
     */
    public String getManuId()
    {
        return this.manuId;
    }

    /**
     *
     * @param id
     */
    public void setId(String id){this.id = id;}

    /**
     *
     * @param type
     */
    public void setType(String type){this.type = type;}

    /**
     *
     * @param base
     */
    public void setBase(String base){this.base = base;}

    /**
     *
     * @param bulb
     */
    public void setBulb(String bulb){this.bulb = bulb;}

    /**
     *
     * @param price
     */
    public void setPrice(int price){this.price = price;}

    /**
     *
     * @param manuId
     */
    public void setManuId(String manuId){this.manuId = manuId;}
}
