package user;

 public class User {
    private String name;
    private String phoneNumber;
    private String address;
    private String city;
    private String country;
    private String aadharCardNumber;

    public User(String name, String phoneNumber, String address, String city, String country, String aadharCardNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.city = city;
        this.country = country;
        this.aadharCardNumber = aadharCardNumber;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getAadharCardNumber() {
        return aadharCardNumber;
    }

    @Override
    public String toString() {
        return "Name: " + name + "\nPhone Number: " + phoneNumber + "\nAddress: " + address +
               "\nCity: " + city + "\nCountry: " + country + "\nAadhar Card Number: " + aadharCardNumber + "\n";
    }
}


