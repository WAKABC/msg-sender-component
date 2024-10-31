package com.wak.msgspringbootstarter.utils;

import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

/**
 * @author wuankang
 * @date 2024/9/3 11:55
 * @Description TODO
 * @Version 1.0
 */
public class CollUtils {

    private CollUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static <T> ArrayList<T> emptyArrayList() {
        return new ArrayList<>();
    }

    public static <K, V> HashMap<K, V> emptyHashMap() {
        return new HashMap<>();
    }

    public static <K, V> LinkedHashMap<K, V> emptyLinkedHashMap() {
        return new LinkedHashMap<>();
    }

    public static <T> HashSet<T> emptyHashSet() {
        return new HashSet<>();
    }

    /**
     * 判断集合是否是为空
     *
     * @param map
     * @return
     */
    public static boolean isEmpty(Map map) {
        return (map == null || map.isEmpty());
    }

    /**
     * 判断集合是否不为空
     *
     * @param map
     * @return
     */
    public static boolean isNotEmpty(Map map) {
        return !isEmpty(map);
    }

    /**
     * 判断集合是否是为空
     *
     * @param coll
     * @return
     */
    public static boolean isEmpty(Collection<?> coll) {
        return (coll == null || coll.isEmpty());
    }

    /**
     * 判断集合是否不为空
     *
     * @param coll
     * @return
     */
    public static boolean isNotEmpty(Collection<?> coll) {
        return !isEmpty(coll);
    }

    /**
     * 判断集合是否不为空
     *
     * @param coll
     * @return
     */
    public static <T> T isNotEmpty(Collection<?> coll, Supplier<T> supplier) {
        if (isNotEmpty(coll)) {
            return supplier.get();
        }
        return null;
    }

    /**
     * 去重
     *
     * @param list
     * @param <T>
     * @return
     */
    public static <T> Collection<T> distinct(Collection<T> list) {
        return isEmpty(list) ? list : distinct(list, item -> item);
    }

    /**
     * 去重
     *
     * @param from
     * @param func
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R> List<R> distinct(Collection<T> from, Function<T, R> func) {
        return distinct(from, func, t -> true);
    }

    /**
     * 去重
     *
     * @param from
     * @param func
     * @param filter
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R> List<R> distinct(Collection<T> from, Function<T, R> func, Predicate<T> filter) {
        if (isEmpty(from)) {
            return new ArrayList<>();
        }
        return from.stream().filter(filter).map(func).distinct().collect(Collectors.toList());
    }

    /**
     * 将一个集合转换为另外一个集合
     *
     * @param from
     * @param func
     * @param <T>
     * @param <U>
     * @return
     */
    public static <T, U> List<U> convertList(Collection<T> from, Function<T, U> func) {
        if (isEmpty(from)) {
            return new ArrayList<>();
        }
        return from.stream().map(func).collect(Collectors.toList());
    }

    /**
     * 将一个集合转换为另外一个集合：from->filter->list
     *
     * @param from
     * @param func
     * @param filter
     * @param <T>
     * @param <U>
     * @return
     */
    public static <T, U> List<U> convertList(Collection<T> from, Function<T, U> func, Predicate<T> filter) {
        if (isEmpty(from)) {
            return new ArrayList<>();
        }
        return from.stream().filter(filter).map(func).collect(Collectors.toList());
    }

    /**
     * 将集合转换为set
     *
     * @param from
     * @param func
     * @param <T>
     * @param <U>
     * @return
     */
    public static <T, U> Set<U> convertSet(Collection<T> from, Function<T, U> func) {
        if (isEmpty(from)) {
            return new HashSet<>();
        }
        return from.stream().map(func).collect(Collectors.toSet());
    }

    /**
     * 将集合转换为set：from->filter->list
     *
     * @param from
     * @param func
     * @param filter
     * @param <T>
     * @param <U>
     * @return
     */
    public static <T, U> Set<U> convertSet(Collection<T> from, Function<T, U> func, Predicate<T> filter) {
        if (isEmpty(from)) {
            return new HashSet<>();
        }
        return from.stream().filter(filter).map(func).collect(Collectors.toSet());
    }

    /**
     * 将集合转换为map
     *
     * @param from
     * @param keyFunc
     * @param <T>
     * @param <K>
     * @return
     */
    public static <T, K> Map<K, T> convertMap(Collection<T> from, Function<T, K> keyFunc) {
        if (isEmpty(from)) {
            return new HashMap<>();
        }
        return convertMap(from, keyFunc, Function.identity());
    }

    /**
     * 将集合转换为map
     *
     * @param from
     * @param keyFunc
     * @param supplier
     * @param <T>
     * @param <K>
     * @return
     */
    public static <T, K> Map<K, T> convertMap(Collection<T> from, Function<T, K> keyFunc, Supplier<? extends Map<K, T>> supplier) {
        if (isEmpty(from)) {
            return supplier.get();
        }
        return convertMap(from, keyFunc, Function.identity(), supplier);
    }

    /**
     * 将集合转换为map
     *
     * @param from
     * @param keyFunc
     * @param valueFunc
     * @param <T>
     * @param <K>
     * @param <V>
     * @return
     */
    public static <T, K, V> Map<K, V> convertMap(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc) {
        if (isEmpty(from)) {
            return new HashMap<>();
        }
        return convertMap(from, keyFunc, valueFunc, (v1, v2) -> v1);
    }

    /**
     * 将集合转换为map
     *
     * @param from
     * @param keyFunc       key转换器
     * @param valueFunc     value转换器
     * @param mergeFunction key重复时value处理策略
     * @param <T>
     * @param <K>
     * @param <V>
     * @return
     */
    public static <T, K, V> Map<K, V> convertMap(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc, BinaryOperator<V> mergeFunction) {
        if (isEmpty(from)) {
            return new HashMap<>();
        }
        return convertMap(from, keyFunc, valueFunc, mergeFunction, HashMap::new);
    }

    /**
     * 将集合转换为map
     *
     * @param from
     * @param keyFunc
     * @param valueFunc
     * @param supplier
     * @param <T>
     * @param <K>
     * @param <V>
     * @return
     */
    public static <T, K, V> Map<K, V> convertMap(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc, Supplier<? extends Map<K, V>> supplier) {
        if (isEmpty(from)) {
            return supplier.get();
        }
        return convertMap(from, keyFunc, valueFunc, (v1, v2) -> v1, supplier);
    }

    /**
     * 将集合转换为map
     *
     * @param from
     * @param keyFunc
     * @param valueFunc
     * @param mergeFunction
     * @param supplier
     * @param <T>
     * @param <K>
     * @param <V>
     * @return
     */
    public static <T, K, V> Map<K, V> convertMap(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc, BinaryOperator<V> mergeFunction, Supplier<? extends Map<K, V>> supplier) {
        if (isEmpty(from)) {
            return new HashMap<>();
        }
        return from.stream().collect(Collectors.toMap(keyFunc, valueFunc, mergeFunction, supplier));
    }

    /**
     * 将集合转换为map
     *
     * @param from
     * @param keyFunc
     * @param <T>
     * @param <K>
     * @return
     */
    public static <T, K> Map<K, List<T>> convertMultiMap(Collection<T> from, Function<T, K> keyFunc) {
        if (isEmpty(from)) {
            return new HashMap<>();
        }
        return from.stream().collect(Collectors.groupingBy(keyFunc, Collectors.mapping(t -> t, Collectors.toList())));
    }

    /**
     * 将集合转换为map
     *
     * @param from
     * @param keyFunc
     * @param <T>
     * @param <K>
     * @return
     */
    public static <T, K, V> Map<K, List<V>> convertMultiMap(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc) {
        if (isEmpty(from)) {
            return new HashMap<>();
        }
        return from.stream()
                .collect(Collectors.groupingBy(keyFunc, Collectors.mapping(valueFunc, Collectors.toList())));
    }

    /**
     * 创建 ArrayList
     *
     * @param args
     * @return
     */
    public static <E> List<E> newArrayList(E... args) {
        return new ArrayList(Arrays.asList(args));
    }

    /**
     * 创建 ArrayList
     *
     * @param initialCapacity
     * @return
     */
    public static <E> List<E> newArrayListCapacity(int initialCapacity) {
        return new ArrayList(initialCapacity);
    }

    /**
     * 创建HashSet
     *
     * @param args
     * @return
     */
    public static <E> Set<E> newHashSet(E... args) {
        return new HashSet<>(Arrays.asList(args));
    }

    /**
     * 创建LinkedHashSet
     *
     * @param args
     * @return
     */
    public static <E> Set<E> newLinkedHashSet(E... args) {
        return new LinkedHashSet<>(Arrays.asList(args));
    }

    /**
     * 创建hashMap
     *
     * @param args
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static <K, V> Map<K, V> newHashMap(Object... args) {
        HashMap paramMap = new HashMap();
        if (args != null) {
            if (args.length % 2 == 0) {
                throw new RuntimeException("The length must be a multiple of 2");
            }
            int size = args.length / 2;
            for (int i = 0; i < size; i++) {
                paramMap.put(args[2 * i], args[2 * i + 1]);
            }
        }
        return paramMap;
    }

    /**
     * 创建LinkedHashMap
     *
     * @param args
     * @return
     */
    public static <K, V> LinkedHashMap<K, V> newLinkedHashMap(Object... args) {
        LinkedHashMap paramMap = new LinkedHashMap();
        if (args != null) {
            if (args.length % 2 == 0) {
                throw new RuntimeException("The length must be a multiple of 2");
            }
            int size = args.length / 2;
            for (int i = 0; i < size; i++) {
                paramMap.put(args[2 * i], args[2 * i + 1]);
            }
        }
        return paramMap;
    }

    /**
     * 都不为空返回true
     *
     * @param values
     * @return
     */
    public static boolean allNotEmpty(final Collection<?>... values) {
        if (values == null) {
            return false;
        }
        for (Collection val : values) {
            if (isEmpty(val)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 都为空，返回true
     *
     * @param values
     * @return
     */
    public static boolean allEmpty(final Collection<?>... values) {
        return !anyNotEmpty(values);
    }

    /**
     * 任意一个不为空，则返回true
     *
     * @param values
     * @return
     */
    public static boolean anyNotEmpty(final Collection<?>... values) {
        return firstNotEmpty(values) != null;
    }

    /**
     * 任意一个为空，则返回true
     *
     * @param values
     * @return
     */
    public static boolean anyEmpty(final Collection<?>... values) {
        return !allNotEmpty(values);
    }

    /**
     * 返回第一个不为空的集合
     *
     * @param values
     * @return
     */
    public static Collection<?> firstNotEmpty(final Collection<?>... values) {
        if (values != null) {
            for (final Collection val : values) {
                if (isNotEmpty(val)) {
                    return val;
                }
            }
        }
        return null;
    }

    /**
     * 返回第一个元素
     *
     * @param list
     * @param <T>
     * @return
     */
    public static <T> T getFirst(Collection<T> list) {
        return isNotEmpty(list) ? list.iterator().next() : null;
    }

    /**
     * 返回第一个元素的某个属性
     *
     * @param list
     * @param <T>
     * @return
     */
    public static <T, R> R getFirst(Collection<T> list, Function<T, R> fun) {
        T first = getFirst(list);
        return first == null ? null : fun.apply(first);
    }

    /**
     * 返回第一个元素的某个属性
     *
     * @param list
     * @param <T>
     * @return
     */
    public static <T> void first(Collection<T> list, Consumer<T> consumer) {
        T first = getFirst(list);
        if (null != first) {
            consumer.accept(first);
        }
    }

    /**
     * 根据 key 获取值
     *
     * @param map
     * @param key
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> V get(Map<K, V> map, K key) {
        return map != null ? map.get(key) : null;
    }

    /**
     * 转换为 Set
     *
     * @param list
     * @param <T>
     * @return
     */
    public static <T> Set<T> convertToSet(Collection<T> list) {
        if (list == null) {
            return null;
        }
        if (Set.class.isInstance(list)) {
            return (Set<T>) list;
        }
        return list.stream().collect(Collectors.toSet());
    }

    /**
     * 转换为 List
     *
     * @param list
     * @param <T>
     * @return
     */
    public static <T> List<T> convertToList(Collection<T> list) {
        if (list == null) {
            return null;
        }
        if (List.class.isInstance(list)) {
            return (List<T>) list;
        }
        return list.stream().collect(Collectors.toList());
    }

    /**
     * list 中是否包含 item
     *
     * @param list
     * @param item
     * @param <T>
     * @return
     */
    public static <T> boolean contain(Collection<T> list, T item) {
        return CollUtils.isNotEmpty(list) && list.contains(item);
    }

    /**
     * 获取集合对象大小
     *
     * @param object
     * @return
     */
    public static int size(final Object object) {
        return CollectionUtils.size(object);
    }


    /**
     * 判断两个{@link Collection} 是否元素和顺序相同，返回{@code true}的条件是：
     * <ul>
     *     <li>两个{@link Collection}必须长度相同</li>
     *     <li>两个{@link Collection}元素相同index的对象必须equals，满足{@link Objects#equals(Object, Object)}</li>
     * </ul>
     * 此方法来自Apache-Commons-Collections4。
     *
     * @param list1 列表1
     * @param list2 列表2
     * @return 是否相同
     */
    public static boolean isEqualOrderList(final Collection<?> list1, final Collection<?> list2) {
        if (list1 == null || list2 == null || list1.size() != list2.size()) {
            return false;
        }
        final Iterator<?> it1 = list1.iterator();
        final Iterator<?> it2 = list2.iterator();
        Object obj1;
        Object obj2;
        while (it1.hasNext() && it2.hasNext()) {
            obj1 = it1.next();
            obj2 = it2.next();

            if (false == Objects.equals(obj1, obj2)) {
                return false;
            }
        }

        // 当两个Iterable长度不一致时返回false
        return false == (it1.hasNext() || it2.hasNext());
    }

    /**
     * 判断两个集合的元素是否一样
     *
     * @param a
     * @param b
     * @param orderEqual 元素顺序是否也要一样？
     * @return
     */
    public static boolean isEqualCollection(final Collection<?> a, final Collection<?> b, boolean orderEqual) {
        if (orderEqual) {
            return isEqualOrderList(a, b);
        }
        return CollectionUtils.isEqualCollection(a, b);
    }
}

