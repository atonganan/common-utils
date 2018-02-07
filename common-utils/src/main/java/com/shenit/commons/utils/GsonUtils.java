package com.shenit.commons.utils;

import com.google.common.collect.BoundType;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Range;
import com.google.gson.*;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.shenit.commons.annotations.IgnoreField;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.*;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parsing json using gson
 * Created by jgnan on 28/12/2016.
 */
public final class
GsonUtils {
    private static final Logger LOG = LoggerFactory.getLogger(GsonUtils.class);
    private static final TypeToken<Map<String,Object>> MAP_TYPE = new TypeToken<Map<String,Object>>(){};
    private static Map<Class<?>,Field<?>[]> REGISTERED_FIELDS = new HashMap<>();
    private static final Gson CUSTOMIZED;
    private static final Gson DEFAULT;
    static{
        CUSTOMIZED = createBuilder()
                .registerTypeAdapter(Boolean.class, new BooleanDeserializer())
                .registerTypeAdapter(new TypeToken<Map <String, Object>>(){}.getType(),  new MapDeserializerDoubleAsIntFix())
                .registerTypeAdapter(boolean.class, new BooleanDeserializer())
                .registerTypeAdapter(Date.class,new DateDeserializer())
                .registerTypeAdapter(Range.class,new RangeCodec())
                .registerTypeAdapter(Type.class,new TypeCodec())
                .registerTypeAdapter(Class.class,new TypeCodec())
                .registerTypeAdapter(String.class,new StringDeserializer())
                .registerTypeAdapter(BigDecimal.class, new BigDecimalSerializer())
                .registerTypeAdapter(Double.class,new DoubleTypeAdapter())
                .registerTypeAdapter(double.class,new DoubleTypeAdapter())
                .registerTypeAdapter(Long.class,new LongTypeAdapter())
                .registerTypeAdapter(long.class,new LongTypeAdapter())
                .setExclusionStrategies(new AnnotationExclusionStrategy()) //format的时候忽略带Exclude注解的字段
                .serializeSpecialFloatingPointValues()
                .create();

        DEFAULT = createBuilder().create();
    }

    /**
     * 获取实例.
     * @return
     */
    public static final Gson inst(){
        return CUSTOMIZED;
    }

    private static final GsonBuilder createBuilder(){
        return new GsonBuilder()
                .setDateFormat("yyyy-mm-dd HH:mm:ss")
                .disableHtmlEscaping()
                .excludeFieldsWithModifiers(Modifier.STATIC, Modifier.TRANSIENT);
    }

    /**
     * Format object to json
     * @param obj Object to format
     * @return
     */
    public static String format(Object obj){
        return obj == null ? null : (obj instanceof String ? (String) obj : CUSTOMIZED.toJson(obj));
    }

    /**
     * Parse json to instance
     * @param str
     * @param _class 类型
     * @param <T>
     * @return
     */
    public static <T> T parse(String str, Class<T> _class){
        try {
            return StringUtils.isEmpty(str) ? null : CUSTOMIZED.fromJson(str, _class);
        }catch(Throwable t){
            //could not parse to json
        }
        return null;
    }


    /**
     * Parse to json
     * @param str Json string
     * @param type Type to cast
     * @param <T>
     * @return
     */
    public static <T> T parse(String str, TypeToken<T> type){
        if(StringUtils.isEmpty(str)) return null;
        try {
            if(str.startsWith(ShenStrings.DOUBLE_QUOTE)){
                //对于Rails的序列化的特殊处理
                str = str.substring(1, str.length() -1);
                str = StringEscapeUtils.unescapeJson(str);
            }
            return StringUtils.isEmpty(str) ? null : CUSTOMIZED.fromJson(str, type.getType());
        }catch(Throwable t){
            if(LOG.isInfoEnabled()) LOG.info("[parse] Could not parse string -> {}",str);
        }
        return null;
    }

    /**
     * 转换为Map类型.
     * @param data
     * @return
     */
    public static Map<String, Object> map(String data) {
        Map<String,Object> map = parse(data,MAP_TYPE);
        return map == null ? Maps.newHashMap() : map;
    }

    /**
     * 获取序列化时使用的字段集合.
     * @param clazz
     * @return
     */
    public static String[] serializeFieldNames(Class<?> clazz){
        if(REGISTERED_FIELDS.containsKey(clazz)) {
            List<String> fieldNames = Lists.newArrayList();
            for(Field field : REGISTERED_FIELDS.get(clazz)){
                fieldNames.add(field.name);
            }
            return fieldNames.toArray(new String[0]);
        }

        serializeFields(clazz); //register first
        return serializeFieldNames(clazz);
    }


    /**
     * 获取序列化时使用的字段集合.
     * @param clazz
     * @return
     */
    public static Field<?>[] serializeFields(Class<?> clazz){
        if(REGISTERED_FIELDS.containsKey(clazz)) return REGISTERED_FIELDS.get(clazz);
        java.lang.reflect.Field[] fields = clazz.getDeclaredFields();
        List<Field<?>> fs = Lists.newArrayList();
        SerializedName serializedName;
        for(java.lang.reflect.Field field : fields) {
            if (field == null || Modifier.isTransient(field.getModifiers())) continue;
            serializedName = field.getAnnotation(SerializedName.class);
            String name = null;
            if (serializedName != null) {
                name = serializedName.value();
            } else {
                name = Modifier.isStatic(field.getModifiers()) ? null : field.getName();
            }
            if(name == null) continue;


            Default defVal = field.getDeclaredAnnotation(Default.class);
            fs.add(new Field(name, field.getName(),field.getType(), defVal == null ? null : defVal.value()));
        }
        Field<?>[] fsArray = fs.toArray(new Field<?>[0]);
        REGISTERED_FIELDS.put(clazz,fsArray);
        return fsArray;
    }

    public static <T> T parse(String val, Type type) {
        if(StringUtils.isEmpty(val)) return null;
        return CUSTOMIZED.fromJson(val,type);
    }

    public static class Field<T>{
        public Field(String name, String originName, Class<T> type,String defaultValue){
            this.name = name;
            this.originName = originName;
            this.type=  type;
            this.defaultValue = defaultValue;
        }
        public String name;
        public String originName;
        public Class<T> type;
        public String defaultValue;

        @Override
        public boolean equals(Object obj) {
            return obj != null && Field.class.isInstance(obj) &&
                    (name != null && name.equals(((Field)obj).name)) ||
                    (originName != null && name.equals(((Field)obj).originName));
        }

        @Override
        public String toString() {
            return StringUtils.wrap(name,'`');
        }
    }


    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    public static @interface Default{
        String value();
    }

    /**
     * For boolean fields
     */
    private static class BooleanDeserializer implements JsonDeserializer<Boolean> {
        public Boolean deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            String value = json.getAsString();
            Boolean result = Boolean.valueOf(value);
            if(StringUtils.isNumeric(value)) result = Integer.parseInt(value) != 0;
            return result;
        }
    }

    /**
     * For Date/Time/Datetime fields
     */
    private static class DateDeserializer implements JsonDeserializer<Date>,JsonSerializer<Date> {
        private static final String EMPTY_ZERO_DATE = "0000-00-00";
        private static final String[] COMMON_DATE_PATTERNS = {
                "yyyy-MM-dd HH:mm:ss","yyyy/MM/dd HH:mm:ss","yyyy-MM-dd HH:mm","yyyy/MM/dd HH:mm",  //for datetime
                "yyyy-MM-dd","yyyy/MM/dd",  //for date only
                "HH:mm:ss","HH:mm"};    //for time only
        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            String value = json.getAsJsonPrimitive().getAsString();
            if(StringUtils.isNumeric(value)) {
                long ts = ShenNumbers.toLong(value, 0l);
                if (ts > 0) return new Date(value.length() == 10 ? ts * 1000 : ts);
            }
            if(value != null && value.startsWith(EMPTY_ZERO_DATE)) return null;
            try {
                return DateUtils.parseDate(value,COMMON_DATE_PATTERNS);
            } catch (ParseException e) {
                if(LOG.isInfoEnabled()) LOG.info("[DateDeserializer.deserialize] Deserialize date string [{}] failed with exception",value,e);
            }
            return null;
        }


        @Override
        public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(DateFormatUtils.format(src,COMMON_DATE_PATTERNS[0]));
        }

    }

    private static class StringDeserializer implements JsonDeserializer<String> {

        @Override
        public String deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return json.isJsonPrimitive() ?  json.getAsJsonPrimitive().getAsString() :  json.toString();
        }
    }

    private static class DoubleTypeAdapter implements JsonSerializer<Double>,JsonDeserializer<Double> {

        @Override
        public JsonElement serialize(Double src, Type typeOfSrc, JsonSerializationContext context) {
            return src == null ? null : new JsonPrimitive(src.doubleValue());
        }

        @Override
        public Double deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return json.isJsonPrimitive() ? json.getAsDouble() : 0d;
        }
    }

    private static class LongTypeAdapter implements JsonSerializer<Long>,JsonDeserializer<Long> {

        @Override
        public JsonElement serialize(Long src, Type typeOfSrc, JsonSerializationContext context) {
            return src == null ? null : new JsonPrimitive(src.longValue());
        }

        @Override
        public Long deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return json.isJsonPrimitive() ? json.getAsLong() : 0l;
        }
    }

    /**
     * For Date/Time/Datetime fields
     */
    private static class RangeCodec implements JsonDeserializer<Range>{
        public Range deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            String value = null;
            if(json.isJsonArray()){
                //对于数组，作为闭区间解释
                JsonArray array = json.getAsJsonArray();
                StringBuilder sb = new StringBuilder();
                sb.append('[')
                        .append(array.get(0));
                if(array.size() > 1) sb.append(',').append(array.get(1));
                sb.append(']');
                value = sb.toString();
            } else {
                value = json.getAsString();
            }
            boolean leftOpen, rightOpen;
            leftOpen = value.startsWith("(");
            rightOpen = value.startsWith(")");
            //因为用的少，无需缓存
            Pattern pattern = Pattern.compile("(?:\\[|\\()([\\d.]+)[,:]([\\d.]+)(?:\\]|\\))");
            Matcher m = pattern.matcher(value);
            if(!m.find()) return null;
            if(m.groupCount() == 0){
                //格式不正确，返回null
                LOG.warn("[RangeDeserializer.deserialize] Illegal format for range -> {}",value);
                return null;
            }
            Comparable start = NumberUtils.toDouble(m.group(1),0.0d);
            Comparable end = m.groupCount() > 1 ? NumberUtils.toDouble(m.group(2),0.0d) : start;
            return Range.range(
                    start, leftOpen ? BoundType.OPEN : BoundType.CLOSED,
                    end, rightOpen ? BoundType.OPEN : BoundType.CLOSED);
        }
    }

    private static class BigDecimalSerializer implements JsonSerializer<BigDecimal>{

        @Override
        public JsonElement serialize(BigDecimal src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.setScale(2,BigDecimal.ROUND_FLOOR).toString());
        }
    }

    /**
     * Map类型的Codec
     */
    private static class MapDeserializerDoubleAsIntFix implements JsonDeserializer<Map<String, Object>> {

        @Override  @SuppressWarnings("unchecked")
        public Map<String, Object> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return (Map<String, Object>) read(json);
        }

        public Object read(JsonElement in) {

            if(in.isJsonArray()){
                List<Object> list = new ArrayList<Object>();
                JsonArray arr = in.getAsJsonArray();
                for (JsonElement anArr : arr) {
                    list.add(read(anArr));
                }
                return list;
            }else if(in.isJsonObject()){
                Map<String, Object> map = new LinkedTreeMap<String, Object>();
                JsonObject obj = in.getAsJsonObject();
                Set<Map.Entry<String, JsonElement>> entitySet = obj.entrySet();
                for(Map.Entry<String, JsonElement> entry: entitySet){
                    map.put(entry.getKey(), read(entry.getValue()));
                }
                return map;
            }else if( in.isJsonPrimitive()){
                JsonPrimitive prim = in.getAsJsonPrimitive();
                if(prim.isBoolean()){
                    return prim.getAsBoolean();
                }else if(prim.isString()){
                    return prim.getAsString();
                }else if(prim.isNumber()){
                    Number num = prim.getAsNumber();
                    // here you can handle double int/long values
                    // and return any type you want
                    // this solution will transform 3.0 float to long values
                    if(Math.ceil(num.doubleValue())  == num.longValue())
                        return num.longValue();
                    else{
                        return num.doubleValue();
                    }
                }
            }
            return null;
        }
    }

    private static class AnnotationExclusionStrategy implements ExclusionStrategy {

        @Override
        public boolean shouldSkipField(FieldAttributes f) {
            return f.getAnnotation(IgnoreField.class) != null;
        }

        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
            return false;
        }
    }


    private static class TypeCodec implements JsonSerializer<Type>,JsonDeserializer<Type>{

        @Override
        public Type deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            String typeName = jsonElement.getAsString();
            try {
                return Class.forName(typeName);
            } catch (ClassNotFoundException e) {
                LOG.warn("[deserialize([jsonElement, type, jsonDeserializationContext])] No class found to type -> {}", typeName);
            }
            return null;
        }

        @Override
        public JsonElement serialize(Type type, Type type2, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(type.getTypeName());
        }
    }

}