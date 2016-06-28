package de.htwdd.vokabeltrainer.helper;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by alex on 6/4/16.
 * Singleton-Klasse
 */
public class LanguageHelper {
    private static LanguageHelper _instance;
    public final HashMap<String, Integer> langCodes;
    public final ArrayList<String> langNames;

    public static LanguageHelper getInstance()
    {
        if (_instance == null) _instance = new LanguageHelper();
        return _instance;
    }

    private LanguageHelper() {
        this.langCodes = new HashMap<String, Integer>();
        this.langNames = new ArrayList<String>();
        int i = 0;

        this.langCodes.put("ab", i++); this.langNames.add("Abkhazian");
        this.langCodes.put("aa", i++); this.langNames.add("Afar");
        this.langCodes.put("af", i++); this.langNames.add("Afrikaans");
        this.langCodes.put("sq", i++); this.langNames.add("Albanian");
        this.langCodes.put("am", i++); this.langNames.add("Amharic");
        this.langCodes.put("ar", i++); this.langNames.add("Arabic");
        this.langCodes.put("an", i++); this.langNames.add("Aragonese");
        this.langCodes.put("hy", i++); this.langNames.add("Armenian");
        this.langCodes.put("as", i++); this.langNames.add("Assamese");
        this.langCodes.put("ae", i++); this.langNames.add("Avestan");
        this.langCodes.put("ay", i++); this.langNames.add("Aymara");
        this.langCodes.put("az", i++); this.langNames.add("Azerbaijani");
        this.langCodes.put("ba", i++); this.langNames.add("Bashkir");
        this.langCodes.put("eu", i++); this.langNames.add("Basque");
        this.langCodes.put("be", i++); this.langNames.add("Belarusian");
        this.langCodes.put("bn", i++); this.langNames.add("Bengali");
        this.langCodes.put("bh", i++); this.langNames.add("Bihari");
        this.langCodes.put("bi", i++); this.langNames.add("Bislama");
        this.langCodes.put("bs", i++); this.langNames.add("Bosnian");
        this.langCodes.put("br", i++); this.langNames.add("Breton");
        this.langCodes.put("bg", i++); this.langNames.add("Bulgarian");
        this.langCodes.put("my", i++); this.langNames.add("Burmese");
        this.langCodes.put("ca", i++); this.langNames.add("Catalan");
        this.langCodes.put("ch", i++); this.langNames.add("Chamorro");
        this.langCodes.put("ce", i++); this.langNames.add("Chechen");
        this.langCodes.put("zh", i++); this.langNames.add("Chinese");
        this.langCodes.put("cu", i++); this.langNames.add("Church Slavic; Slavonic; Old Bulgarian");
        this.langCodes.put("cv", i++); this.langNames.add("Chuvash");
        this.langCodes.put("kw", i++); this.langNames.add("Cornish");
        this.langCodes.put("co", i++); this.langNames.add("Corsican");
        this.langCodes.put("hr", i++); this.langNames.add("Croatian");
        this.langCodes.put("cs", i++); this.langNames.add("Czech");
        this.langCodes.put("da", i++); this.langNames.add("Danish");
        this.langCodes.put("dv", i++); this.langNames.add("Divehi; Dhivehi; Maldivian");
        this.langCodes.put("nl", i++); this.langNames.add("Dutch");
        this.langCodes.put("dz", i++); this.langNames.add("Dzongkha");
        this.langCodes.put("en", i++); this.langNames.add("English");
        this.langCodes.put("eo", i++); this.langNames.add("Esperanto");
        this.langCodes.put("et", i++); this.langNames.add("Estonian");
        this.langCodes.put("fo", i++); this.langNames.add("Faroese");
        this.langCodes.put("fj", i++); this.langNames.add("Fijian");
        this.langCodes.put("fi", i++); this.langNames.add("Finnish");
        this.langCodes.put("fr", i++); this.langNames.add("French");
        this.langCodes.put("gd", i++); this.langNames.add("Gaelic; Scottish Gaelic");
        this.langCodes.put("gl", i++); this.langNames.add("Galician");
        this.langCodes.put("ka", i++); this.langNames.add("Georgian");
        this.langCodes.put("de", i++); this.langNames.add("German");
        this.langCodes.put("el", i++); this.langNames.add("Greek, Modern (1453-)");
        this.langCodes.put("gn", i++); this.langNames.add("Guarani");
        this.langCodes.put("gu", i++); this.langNames.add("Gujarati");
        this.langCodes.put("ht", i++); this.langNames.add("Haitian; Haitian Creole ");
        this.langCodes.put("ha", i++); this.langNames.add("Hausa");
        this.langCodes.put("he", i++); this.langNames.add("Hebrew");
        this.langCodes.put("hz", i++); this.langNames.add("Herero");
        this.langCodes.put("hi", i++); this.langNames.add("Hindi");
        this.langCodes.put("ho", i++); this.langNames.add("Hiri Motu");
        this.langCodes.put("hu", i++); this.langNames.add("Hungarian");
        this.langCodes.put("is", i++); this.langNames.add("Icelandic");
        this.langCodes.put("io", i++); this.langNames.add("Ido");
        this.langCodes.put("id", i++); this.langNames.add("Indonesian");
        this.langCodes.put("ia", i++); this.langNames.add("Interlingua (International Auxiliary Language Association)");
        this.langCodes.put("ie", i++); this.langNames.add("Interlingue");
        this.langCodes.put("iu", i++); this.langNames.add("Inuktitut");
        this.langCodes.put("ik", i++); this.langNames.add("Inupiaq");
        this.langCodes.put("ga", i++); this.langNames.add("Irish");
        this.langCodes.put("it", i++); this.langNames.add("Italian");
        this.langCodes.put("ja", i++); this.langNames.add("Japanese");
        this.langCodes.put("jv", i++); this.langNames.add("Javanese");
        this.langCodes.put("kl", i++); this.langNames.add("Kalaallisut");
        this.langCodes.put("kn", i++); this.langNames.add("Kannada");
        this.langCodes.put("ks", i++); this.langNames.add("Kashmiri");
        this.langCodes.put("kk", i++); this.langNames.add("Kazakh");
        this.langCodes.put("km", i++); this.langNames.add("Khmer");
        this.langCodes.put("ki", i++); this.langNames.add("Kikuyu; Gikuyu");
        this.langCodes.put("rw", i++); this.langNames.add("Kinyarwanda");
        this.langCodes.put("ky", i++); this.langNames.add("Kirghiz");
        this.langCodes.put("kv", i++); this.langNames.add("Komi");
        this.langCodes.put("ko", i++); this.langNames.add("Korean");
        this.langCodes.put("kj", i++); this.langNames.add("Kuanyama; Kwanyama");
        this.langCodes.put("ku", i++); this.langNames.add("Kurdish");
        this.langCodes.put("lo", i++); this.langNames.add("Lao");
        this.langCodes.put("la", i++); this.langNames.add("Latin");
        this.langCodes.put("lv", i++); this.langNames.add("Latvian");
        this.langCodes.put("li", i++); this.langNames.add("Limburgan; Limburger; Limburgish");
        this.langCodes.put("ln", i++); this.langNames.add("Lingala");
        this.langCodes.put("lt", i++); this.langNames.add("Lithuanian");
        this.langCodes.put("lb", i++); this.langNames.add("Luxembourgish; Letzeburgesch");
        this.langCodes.put("mk", i++); this.langNames.add("Macedonian");
        this.langCodes.put("mg", i++); this.langNames.add("Malagasy");
        this.langCodes.put("ms", i++); this.langNames.add("Malay");
        this.langCodes.put("ml", i++); this.langNames.add("Malayalam");
        this.langCodes.put("mt", i++); this.langNames.add("Maltese");
        this.langCodes.put("gv", i++); this.langNames.add("Manx");
        this.langCodes.put("mi", i++); this.langNames.add("Maori");
        this.langCodes.put("mr", i++); this.langNames.add("Marathi");
        this.langCodes.put("mh", i++); this.langNames.add("Marshallese");
        this.langCodes.put("mo", i++); this.langNames.add("Moldavian");
        this.langCodes.put("mn", i++); this.langNames.add("Mongolian");
        this.langCodes.put("na", i++); this.langNames.add("Nauru");
        this.langCodes.put("nv", i++); this.langNames.add("Navaho, Navajo");
        this.langCodes.put("nd", i++); this.langNames.add("Ndebele, North");
        this.langCodes.put("nr", i++); this.langNames.add("Ndebele, South");
        this.langCodes.put("ng", i++); this.langNames.add("Ndonga");
        this.langCodes.put("ne", i++); this.langNames.add("Nepali");
        this.langCodes.put("se", i++); this.langNames.add("Northern Sami");
        this.langCodes.put("no", i++); this.langNames.add("Norwegian");
        this.langCodes.put("nb", i++); this.langNames.add("Norwegian Bokmal");
        this.langCodes.put("nn", i++); this.langNames.add("Norwegian Nynorsk");
        this.langCodes.put("ny", i++); this.langNames.add("Nyanja; Chichewa; Chewa");
        this.langCodes.put("oc", i++); this.langNames.add("Occitan (post 1500); Provencal");
        this.langCodes.put("or", i++); this.langNames.add("Oriya");
        this.langCodes.put("om", i++); this.langNames.add("Oromo");
        this.langCodes.put("os", i++); this.langNames.add("Ossetian; Ossetic");
        this.langCodes.put("pi", i++); this.langNames.add("Pali");
        this.langCodes.put("pa", i++); this.langNames.add("Panjabi");
        this.langCodes.put("fa", i++); this.langNames.add("Persian");
        this.langCodes.put("pl", i++); this.langNames.add("Polish");
        this.langCodes.put("pt", i++); this.langNames.add("Portuguese");
        this.langCodes.put("ps", i++); this.langNames.add("Pushto");
        this.langCodes.put("qu", i++); this.langNames.add("Quechua");
        this.langCodes.put("rm", i++); this.langNames.add("Raeto-Romance");
        this.langCodes.put("ro", i++); this.langNames.add("Romanian");
        this.langCodes.put("rn", i++); this.langNames.add("Rundi");
        this.langCodes.put("ru", i++); this.langNames.add("Russian");
        this.langCodes.put("sm", i++); this.langNames.add("Samoan");
        this.langCodes.put("sg", i++); this.langNames.add("Sango");
        this.langCodes.put("sa", i++); this.langNames.add("Sanskrit");
        this.langCodes.put("sc", i++); this.langNames.add("Sardinian");
        this.langCodes.put("sr", i++); this.langNames.add("Serbian");
        this.langCodes.put("sn", i++); this.langNames.add("Shona");
        this.langCodes.put("ii", i++); this.langNames.add("Sichuan Yi");
        this.langCodes.put("sd", i++); this.langNames.add("Sindhi");
        this.langCodes.put("si", i++); this.langNames.add("Sinhala; Sinhalese");
        this.langCodes.put("sk", i++); this.langNames.add("Slovak");
        this.langCodes.put("sl", i++); this.langNames.add("Slovenian");
        this.langCodes.put("so", i++); this.langNames.add("Somali");
        this.langCodes.put("st", i++); this.langNames.add("Sotho, Southern");
        this.langCodes.put("es", i++); this.langNames.add("Spanish; Castilian");
        this.langCodes.put("su", i++); this.langNames.add("Sundanese");
        this.langCodes.put("sw", i++); this.langNames.add("Swahili");
        this.langCodes.put("ss", i++); this.langNames.add("Swati");
        this.langCodes.put("sv", i++); this.langNames.add("Swedish");
        this.langCodes.put("tl", i++); this.langNames.add("Tagalog");
        this.langCodes.put("ty", i++); this.langNames.add("Tahitian");
        this.langCodes.put("tg", i++); this.langNames.add("Tajik");
        this.langCodes.put("ta", i++); this.langNames.add("Tamil");
        this.langCodes.put("tt", i++); this.langNames.add("Tatar");
        this.langCodes.put("te", i++); this.langNames.add("Telugu");
        this.langCodes.put("th", i++); this.langNames.add("Thai");
        this.langCodes.put("bo", i++); this.langNames.add("Tibetan");
        this.langCodes.put("ti", i++); this.langNames.add("Tigrinya");
        this.langCodes.put("to", i++); this.langNames.add("Tonga (Tonga Islands)");
        this.langCodes.put("ts", i++); this.langNames.add("Tsonga");
        this.langCodes.put("tn", i++); this.langNames.add("Tswana");
        this.langCodes.put("tr", i++); this.langNames.add("Turkish");
        this.langCodes.put("tk", i++); this.langNames.add("Turkmen");
        this.langCodes.put("tw", i++); this.langNames.add("Twi");
        this.langCodes.put("ug", i++); this.langNames.add("Uighur");
        this.langCodes.put("uk", i++); this.langNames.add("Ukrainian");
        this.langCodes.put("ur", i++); this.langNames.add("Urdu");
        this.langCodes.put("uz", i++); this.langNames.add("Uzbek");
        this.langCodes.put("vi", i++); this.langNames.add("Vietnamese");
        this.langCodes.put("vo", i++); this.langNames.add("Volapuk");
        this.langCodes.put("wa", i++); this.langNames.add("Walloon");
        this.langCodes.put("cy", i++); this.langNames.add("Welsh");
        this.langCodes.put("fy", i++); this.langNames.add("Western Frisian");
        this.langCodes.put("wo", i++); this.langNames.add("Wolof");
        this.langCodes.put("xh", i++); this.langNames.add("Xhosa");
        this.langCodes.put("yi", i++); this.langNames.add("Yiddish");
        this.langCodes.put("yo", i++); this.langNames.add("Yoruba");
        this.langCodes.put("za", i++); this.langNames.add("Zhuang; Chuang");
        this.langCodes.put("zu", i++); this.langNames.add("Zulu");
    }

    public ArrayList<String> getLanguageNames() {
        return this.langNames;
    }

    public String getLanguageNameByCode(String code) {
        return this.langNames.get(this.langCodes.get(code));
    }

    /*
     * Liefert bei Index 0 z. B. "ab".
     */
    public String getLanguageCodeByIndex(int index) {
        String code = "";

        for (Map.Entry<String, Integer> entry : this.langCodes.entrySet()) {
            if (entry.getValue() == index) {
                code = entry.getKey();
                break;
            }
        }

        return code;
    }
}
