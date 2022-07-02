package pl.sda.arppl4.parser;

import pl.sda.arppl4.dao.ProductDao;
import pl.sda.arppl4.model.Product;
import pl.sda.arppl4.model.ProductUnit;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ProductCommandLineParser {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyy-MM-dd");
    private final Scanner scanner;
    private final ProductDao dao;

    public ProductCommandLineParser(Scanner scanner, ProductDao dao) {
        this.scanner = scanner;
        this.dao = dao;
    }

    public void parse() {
        String command = null;
        do {
            System.out.println("Command: add, list, delete, show, edit");
            command = scanner.next();

            if (command.equalsIgnoreCase("add")) {
                handleAddCommand();   // obsługa komendy dodaj
            } else if (command.equalsIgnoreCase("list")) {
                handleListCommand();
            } else if (command.equalsIgnoreCase("delete")) {
                handleDeleteCommand();
            } else if (command.equalsIgnoreCase("show")) {
                handleShowCommand();
            } else if (command.equalsIgnoreCase("edit")) {
                handleEditCommand();
            }

        } while (!command.equalsIgnoreCase("quit"));
    }

    private void handleEditCommand() {
        System.out.println("Provide product id");
        Long podaneId = scanner.nextLong();
        Optional<Product> szukanyProdukt = dao.zwrocProduct(podaneId);

        if (szukanyProdukt.isPresent()) {
            Product wybranyProdukt = szukanyProdukt.get();

            System.out.println("Which parameter You want to update");
            String output = scanner.next();

            switch (output) {
                case "price":
                    System.out.println("Provide new price");
                    Double price = scanner.nextDouble();
                    wybranyProdukt.setPrice(price);
                    break;
                case "expiryDate":
                    LocalDate expiryDate = loadExpiryDateFromUser();
                    wybranyProdukt.setExpiryDate(expiryDate);
                    break;
                case "quantity":
                    System.out.println("Provide new quantity");
                    Double quantity = scanner.nextDouble();
                    wybranyProdukt.setQuantity(quantity);
                    break;
                default:
                    System.out.println("Field with this name is not handled");
            }
            dao.updateProduct(wybranyProdukt);
            System.out.println("Product has been updated");
        } else {
            System.out.println("Product not found");
        }
    }

        private void handleShowCommand () {
            System.out.println("Provide product id:");
            Long podaneId = scanner.nextLong();
            Optional<Product> szukanyProdukt = dao.zwrocProduct(podaneId);
            if (szukanyProdukt.isPresent()) {
                System.out.println("Szukany produkt został odnaleziony: " + szukanyProdukt);
            }
        }

        private void handleDeleteCommand () {
            System.out.println("Provide product id:");
            Long podaneId = scanner.nextLong();
            Optional<Product> szukanyProdukt = dao.zwrocProduct(podaneId);
            if (szukanyProdukt.isPresent()) {
                Product product = szukanyProdukt.get();
                dao.usunProduct(product);
                System.out.println("Product removed");
            } else {
                System.out.println("Product not found");
            }
        }

        private void handleListCommand () {
            List<Product> productList = dao.zwrocListeProducts();
            for (Product product : productList) {
                System.out.println(product);
            }
            System.out.println();
        }

        private void handleAddCommand () {
            System.out.println("Provide name:");
            String name = scanner.next();

            System.out.println("Provide price:");
            Double price = scanner.nextDouble();

            System.out.println("Provide producent:");
            String producent = scanner.next();

            LocalDate expiryDate = loadExpiryDateFromUser();

            System.out.println("Provide quantity:");
            Double quantity = scanner.nextDouble();

            ProductUnit productUnit = loadProductUnitFromUser();

            Product product = new Product(null, name, price, producent, expiryDate, quantity, productUnit);
            dao.dodajProduct(product);
        }

        private ProductUnit loadProductUnitFromUser () {
            ProductUnit productUnit = null;
            do {
                try {
                    System.out.println("Provide unit:");
                    String unitString = scanner.next();

                    productUnit = ProductUnit.valueOf(unitString.toUpperCase());
                } catch (IllegalArgumentException iae) {
                    System.err.println("Wrong unit, please provide unit from: ...");
                }
            } while (productUnit == null);
            return productUnit;
        }

        private LocalDate loadExpiryDateFromUser () {
            LocalDate expiryDate = null;
            do {
                try {
                    System.out.println("Provide expiry date:");
                    String expiryDateString = scanner.next();

                    //   DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyy-MM-dd");
                    expiryDate = LocalDate.parse(expiryDateString, FORMATTER);

                        // ten blok nie działa... nie wiem dlaczego...
                    LocalDate today = LocalDate.now();
                    if (expiryDate.isBefore(today)) {
                        // błąd, expiry date jest przed dzisiejszym dniem
                        throw new IllegalArgumentException("Date is before today.");
                    }

                } catch (IllegalArgumentException | DateTimeException iae) {
                    expiryDate = null;
                    System.err.println("Wrong date, please provide date in format: yyy-MM-dd");
                }
            } while (expiryDate == null);
            return expiryDate;
        }
    }
