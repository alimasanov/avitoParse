package com.alimasanov.avitoparse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) throws Exception {
        LinkedHashSet<String> newPageArr = new LinkedHashSet<>();   //хранит номера телефонов
        ArrayList<String> pageArr = new ArrayList<>();              //хранит страницу с объявлением
        ArrayList<String> arr1 = new ArrayList<>();                 //промежуточный список
        ArrayList<String> arr = new ArrayList<>();                  //хранит список ссылок на объявления с одной страницы
        ArrayList<String> links = new ArrayList<>();                //хранит список ссылок на объявления со всех страниц

        for (int i = 201; i <= 220; i++) {                             //i - номер страницы с объявлением
            parse(arr, i);
            theChoiceOfLinesOfACertainLength(arr, arr1);
            sortListOfLinks(arr);
            arrays(arr, links);
            parsePage(pageArr, newPageArr, links);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {}

            try{
                FileWriter writer = new FileWriter("Numbers2.txt");
                for (String s : newPageArr) {
                    writer.write(s + "\n");
                }
                writer.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            System.out.println(i);
        }


    }

    private static void arrays (ArrayList<String> arr, ArrayList<String> links) {
        links.clear();
        for (String s : arr) {
            links.add(s);
        }
        arr.clear();
    }

    //парсит ссылки на объявления
    private static void parse (ArrayList<String> arr, int numPage) throws Exception {
        arr.clear();
        String b = String.valueOf(numPage);
        Document document = Jsoup.connect("https://m.avito.ru/respublika_krym/predlozheniya_uslug/remont_stroitelstvo?p=" + b).get();

        Elements elements = document.select("a");

        for (Element element : elements) {
            arr.add(element.attr("href"));
        }
    }

    //удаление ссылок на кнопки перехода по страницам
    private static void sortListOfLinks(ArrayList<String> arr) {
        if (arr.size() > 23) {
            int in = arr.size() - 23;
            for (int i = 0; i < in; i++) {
                arr.remove(arr.size() - 1);
            }
        }
    }

    //удаление первых двух ссылок и последней
    //первые две - ссылки на поисковую строку
    //последняя - перенаправление на полную версию сайта
    private static void removeElementsFromArrayList(ArrayList<String> arrayList) {
        arrayList.remove(0);
        arrayList.remove(0);
        arrayList.remove(arrayList.size() - 1);
    }

    //отсеивание коротких строк
    private static void theChoiceOfLinesOfACertainLength(ArrayList<String> arrayList, ArrayList<String> arrayList1) {
        for (String s : arrayList) {
            if(s.length() > 43){
                arrayList1.add(s);
            }
        }
        arrayList.clear();

        for (String s : arrayList1) {
            arrayList.add(s);
        }
        arrayList1.clear();

        removeElementsFromArrayList(arrayList);
    }

    //парсит страницу с объявлением
    private static void parsePage(ArrayList<String> pageArr, LinkedHashSet<String> newPageArr, ArrayList<String> links) throws Exception {
        for (String link : links) {

            Document document = Jsoup.connect("https://m.avito.ru" + link).get();
            Elements elements = document.select("a");

            for (Element element : elements) {
                pageArr.add(element.attr("href"));
            }
            findTel(pageArr, newPageArr);
        }
    }

    //ищет номер телефон на странице с объявлением
    private static void findTel(ArrayList<String> pageArr, LinkedHashSet<String> newPageArr) {
        Pattern p = Pattern.compile("^(tel:)((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,10}$");
        for (String s : pageArr) {
            Matcher m = p.matcher(s);
            if (m.matches() == true) {
                System.out.println(s);
                newPageArr.add(s);
                break;
            }
        }
        pageArr.clear();
    }
}