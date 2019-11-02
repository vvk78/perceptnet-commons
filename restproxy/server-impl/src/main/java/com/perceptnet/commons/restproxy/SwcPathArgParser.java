package com.perceptnet.commons.restproxy;



import java.util.Collection;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 29.01.2018
 */
class SwcPathArgParser {
//    Object parsePathItem(ObjectInfo pd, String pathItem) throws SwcArgParsingException {
//        if (pathItem == null || pathItem.equals("null")) {
//            return null;
//        }
//        if (pd.isCollection()) {
//            return parseCollectionArg(pd, pathItem);
//        } else {
//            Class clazz = pd.getClazz();
//            if (clazz == String.class) {
//                if (pathItem.equals("'null'")) {
//                    return "null";
//                }
//                return StringEscapeUtils.unescapeJson(pathItem);
//            } else {
//                if (pathItem.isEmpty()) {
//                    return null;
//                }
//                if (Id.class.isAssignableFrom(clazz)) {
//                    if (pd.getTypeParams().isEmpty()) {
//                        throw new UnsupportedOperationException("Cannot parse untyped id"); //todo implement with string parsing
//                    }
//                    Class entityClass = (Class) pd.getTypeParams().get(0);
//                    throw new UnsupportedOperationException("Ids are not supported");
//                    //return PolyglotSpaceIdFactory.getInstance().createPersistedObjectId(entityClass, Long.valueOf(pathItem));
//                } else {
//                    return ParseUtils.parseUnsafely(pathItem, clazz);
//                }
//            }
//        }
//    }
//
//    Collection parseCollectionArg(ObjectInfo pd, String pathItem) throws SwcArgParsingException {
//        ObjectInfo ciInfo = pd.getCollectionItemInfo();
//        boolean isId = Id.class.isAssignableFrom(ciInfo.getClazz());
//        if (ciInfo.getClazz() == String.class || !ciInfo.isFlat() && !isId) {
//            throw new IllegalStateException("Cannot parse " + pd + " as path arg");
//        }
//        Collection result = (Collection) ClassUtils.createInstance(pd.getClazz());
//        if (pathItem.isEmpty()) {
//            return result;
//        }
//        if (isId) {
//            if (ciInfo.getTypeParams().isEmpty()) {
//                throw new UnsupportedOperationException("Cannot parse untyped id collection"); //todo implement with string parsing
//            }
//            Class entityClass = (Class) ciInfo.getTypeParams().get(0);
//            String[] idStrings = pathItem.split(",");
//            for (String idString : idStrings) {
//                throw new UnsupportedOperationException("Ids are not supported");
////                Id id = PolyglotSpaceIdFactory.getInstance().createPersistedObjectId(entityClass, Long.valueOf(idString));
////                result.add(id);
//            }
//            return result;
//        } else {
//            String[] items = pathItem.split(",");
//            for (String item : items) {
//                result.add(ParseUtils.parseUnsafely(item, ciInfo.getClazz()));
//            }
//        }
//        return null;
//    }

}
