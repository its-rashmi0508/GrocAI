package com.example.grocerymate.util;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Maps a grocery item's name to a matching emoji "icon".
 * Covers common vegetables, fruits, dairy, grains and meats, and falls back to
 * a per-category icon when the exact food isn't recognised. Matching is a simple
 * case-insensitive "contains", so specific / multi-word keys are listed first.
 */
public final class FoodIcons {

    private FoodIcons() { }

    private static final Map<String, String> MAP = new LinkedHashMap<>();

    private static void put(String k, String v) { MAP.put(k, v); }

    static {
        // --- specific / conflict-prone keys must come first ---
        put("sweet potato", "🍠");
        put("red onion", "🧅");
        put("green onion", "🧅");
        put("spring onion", "🧅");
        put("red cabbage", "🥬");
        put("bell pepper", "🫑");
        put("red chili", "🌶️");
        put("chili pepper", "🌶️");
        put("chilli", "🌶️");
        put("custard apple", "🍏");
        put("cashew", "🥜");
        put("cottage cheese", "🧀");
        put("cream cheese", "🧀");
        put("soft cheese", "🧀");
        put("ice cream", "🍦");
        put("whipped cream", "🍦");
        put("sour cream", "🥛");
        put("hot chocolate", "☕");
        put("condensed milk", "🥫");
        put("powdered milk", "🥛");
        put("buttermilk", "🥛");
        put("milkshake", "🥤");
        put("goat meat", "🥩");
        put("goat", "🥩");
        put("horse meat", "🐴");
        put("dragon fruit", "🍈");

        // --- vegetables ---
        put("leek", "🥬");
        put("yam", "🍠");
        put("parsnip", "🥕");
        put("garlic", "🧄");
        put("carrot", "🥕");
        put("green bean", "🫛");
        put("tomato", "🍅");
        put("cucumber", "🥒");
        put("eggplant", "🍆");
        put("aubergine", "🍆");
        put("beet", "🥬");
        put("pumpkin", "🎃");
        put("mushroom", "🍄");
        put("spinach", "🥬");
        put("potato", "🥔");
        put("cabbage", "🥬");
        put("lettuce", "🥬");
        put("onion", "🧅");
        put("broccoli", "🥦");
        put("artichoke", "🥬");
        put("celery", "🥬");
        put("asparagus", "🥬");
        put("radish", "🥬");
        put("zucchini", "🥒");
        put("courgette", "🥒");
        put("capsicum", "🫑");
        put("pepper", "🫑");
        put("cauliflower", "🥦");
        put("collard", "🥬");
        put("chayote", "🥒");
        put("endive", "🥬");
        put("okra", "🌿");
        put("ginger", "🫚");
        put("kale", "🥬");
        put("peas", "🫛");

        // --- fruits (specific before generic: pineapple>apple, grapefruit>grape, watermelon>melon) ---
        put("pineapple", "🍍");
        put("grapefruit", "🍊");
        put("watermelon", "🍉");
        put("strawberry", "🍓");
        put("blueberry", "🫐");
        put("raspberry", "🍓");
        put("cranberry", "🫐");
        put("gooseberry", "🫐");
        put("mulberry", "🫐");
        put("elderberry", "🫐");
        put("grape", "🍇");
        put("mango", "🥭");
        put("orange", "🍊");
        put("plum", "🍑");
        put("apricot", "🍑");
        put("avocado", "🥑");
        put("guava", "🍐");
        put("sapodilla", "🥝");
        put("lychee", "🍒");
        put("pomegranate", "🍎");
        put("peach", "🍑");
        put("peanut", "🥜");
        put("pear", "🍐");
        put("pea", "🫛");
        put("apple", "🍎");
        put("cherry", "🍒");
        put("melon", "🍈");
        put("banana", "🍌");
        put("damson", "🍇");
        put("papaya", "🥭");
        put("coconut", "🥥");
        put("shaddock", "🍊");
        put("durian", "🍈");
        put("kiwi", "🥝");
        put("jackfruit", "🍈");
        put("jamun", "🫐");
        put("persimmon", "🍊");
        put("quince", "🍏");
        put("currant", "🍇");
        put("jujube", "🌰");
        put("olive", "🫒");
        put("tamarind", "🌰");
        put("sugarcane", "🎋");
        put("date", "🌴");
        put("fig", "🍈");
        put("carambola", "⭐");
        put("lemon", "🍋");
        put("lime", "🍋");
        put("berry", "🫐");

        // --- dairy ---
        put("butter", "🧈");
        put("paneer", "🧀");
        put("feta", "🧀");
        put("cheese", "🧀");
        put("yogurt", "🥛");
        put("yoghurt", "🥛");
        put("chocolate", "🍫");
        put("pudding", "🍮");
        put("candy", "🍬");
        put("margarine", "🧈");
        put("quark", "🧀");
        put("gelato", "🍨");
        put("whey", "🥛");
        put("ghee", "🧈");
        put("curd", "🥛");
        put("ice pop", "🍡");
        put("sherbet", "🍧");
        put("custard", "🍮");
        put("coleslaw", "🥗");
        put("samosa", "🥟");
        put("kefir", "🥛");
        put("cream", "🍦");
        put("milk", "🥛");

        // --- grains (buckwheat before wheat) ---
        put("buckwheat", "🌾");
        put("wheat", "🌾");
        put("farro", "🌾");
        put("quinoa", "🌾");
        put("bulgur", "🌾");
        put("amaranth", "🌾");
        put("spelt", "🌾");
        put("oat", "🌾");
        put("barley", "🌾");
        put("rice", "🍚");
        put("rye", "🌾");
        put("millet", "🌾");
        put("bread", "🍞");
        put("flour", "🌾");
        put("pasta", "🍝");
        put("noodle", "🍜");
        put("cereal", "🥣");
        put("corn", "🌽");
        put("maize", "🌽");
        put("dal", "🫘");
        put("lentil", "🫘");
        put("chickpea", "🫘");
        put("bean", "🫘");

        // --- meats & seafood (egg handled after eggplant above) ---
        put("beef", "🥩");
        put("lamb", "🍖");
        put("mutton", "🍖");
        put("pork", "🥓");
        put("veal", "🥩");
        put("venison", "🦌");
        put("bison", "🐃");
        put("chicken", "🍗");
        put("turkey", "🦃");
        put("duck", "🦆");
        put("goose", "🦢");
        put("pheasant", "🍗");
        put("quail", "🍗");
        put("salmon", "🐟");
        put("tuna", "🐟");
        put("cod", "🐟");
        put("mackerel", "🐟");
        put("trout", "🐟");
        put("shrimp", "🦐");
        put("prawn", "🦐");
        put("octopus", "🐙");
        put("rabbit", "🐰");
        put("kangaroo", "🦘");
        put("fish", "🐟");
        put("bacon", "🥓");
        put("sausage", "🌭");
        put("crab", "🦀");
        put("lobster", "🦞");
        put("steak", "🥩");
        put("mince", "🥩");
        put("ham", "🍖");
        put("meat", "🥩");
        put("egg", "🥚");

        // --- common pantry ---
        put("oil", "🫙");
        put("sugar", "🍬");
        put("salt", "🧂");
        put("honey", "🍯");
        put("tea", "🍵");
        put("coffee", "☕");
        put("juice", "🧃");
        put("water", "💧");
        put("almond", "🌰");
        put("nut", "🥜");
        put("cookie", "🍪");
        put("biscuit", "🍪");
        put("chip", "🍟");
        put("snack", "🍿");
    }

    /** Returns an emoji for the given item, using the category as a fallback. */
    public static String emojiFor(String name, String category) {
        String n = name == null ? "" : name.toLowerCase(Locale.ROOT).trim();
        for (Map.Entry<String, String> e : MAP.entrySet()) {
            if (n.contains(e.getKey())) return e.getValue();
        }
        return categoryFallback(category);
    }

    private static String categoryFallback(String category) {
        if (category == null) return "🛒";
        switch (category) {
            case "Vegetables": return "🥬";
            case "Fruits":     return "🍎";
            case "Dairy":      return "🥛";
            case "Grains":     return "🌾";
            case "Meats":      return "🍖";
            default:           return "🛒";
        }
    }
}
