package com.perceptnet.commons.restproxy;

import com.perceptnet.commons.reflection.ReflectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 02.11.2019
 */
public class ServiceWebCaller {
//    private Logger log = LoggerFactory.getLogger(getClass());
//
//    private ReflectionProvider refProvider;
//    private Map<String, ServiceDescription> services;
//    private SwcPathArgParser pathArgParser;
//
//
//    public ServiceWebCaller(ReflectionProvider refProvider, Map<String, ServiceDescription> services) {
//        this.services = services;
//        this.pathArgParser = new SwcPathArgParser();
//        this.refProvider = refProvider;
//    }
//
//    public Object callService(String requestPath, String requestBody) {
//        String[] pieces = requestPath.split("/");
//        if (requestPath.length() < 2) {
//            throw new RuntimeException("Not enough path pieces (" + pieces.length + ")");
//        }
//
//        String serviceName = pieces[0];
//        ServiceDescription sd = services.get(serviceName);
//        if (sd == null) {
//            throw new NoSuchElementException("No service " + serviceName);
//        }
//        String methodName = pieces[1];
//        List<MethodDescription> overloaded = sd.getMethods().get(methodName);
//        if (overloaded == null || overloaded.isEmpty()) {
//            throw new NoSuchElementException("No method " + methodName + " in service " + serviceName);
//        }
//        List<String> methodRawArgs = getUrlDecodedPathArgs(pieces);
//        if (overloaded.size() == 1) {
//            MethodDescription m = overloaded.get(0);
//            List<Object> args = parseMethodArguments(m, methodRawArgs, requestBody);
//            if (args == null) {
//                throw new RuntimeException("Cannot parse arguments for service method " + serviceName + "." + methodName);
//            }
//            return callServiceMethod(sd.getServiceImpl(), m, args);
//        } else {
//            for (MethodDescription m : overloaded) {
//                List<Object> args = null;
//                try {
//                    args = parseMethodArguments(m, methodRawArgs, requestBody);
//                } catch (Exception ignore) {
//                }
//                if (args != null) {
//                    return callServiceMethod(sd.getServiceImpl(), m, args);
//                }
//            }
//            throw new RuntimeException("Cannot parse arguments for none of " + overloaded.size() + " service methods " + serviceName + "." + methodName);
//        }
//
////        return null;
//    }
//
//    private List<String> getUrlDecodedPathArgs(String[] pieces) {
//        List<String> result = new ArrayList<>(pieces.length - 2);
//        for (int i = 2; i < pieces.length; i++) {
//            try {
//                result.add(URLDecoder.decode(pieces[i], "UTF-8"));
//            } catch (UnsupportedEncodingException e) {
//                throw new RuntimeException(e);
//            }
//        }
//        return result;
//    }
//
//
//    Object callServiceMethod(Object impl, MethodDescription m, List<Object> args) {
//        try {
//            Object result = m.getMethod().invoke(impl, args.toArray());
//            return result;
//        } catch (IllegalAccessException e) {
//            throw new RuntimeException("IllegalAccessException: " + e, e);
//        } catch (InvocationTargetException e) {
//            if (e.getTargetException() instanceof RuntimeException) {
//                throw ((RuntimeException) e.getTargetException());
//            } else if (e.getTargetException() instanceof Error) {
//                throw ((Error) e.getTargetException());
//            } else {
//                throw new RuntimeException("InvocationTargetException: " + e, e);
//            }
//        }
//    }
//
//    List parseMethodArguments(MethodDescription m, List<String> argPathPieces, String requestBody) {
//        if (m.getParams().isEmpty()) {
//            return argPathPieces.isEmpty() ? Collections.emptyList() : null;
//        }
//        if (m.isAllParamsFromBody() || argPathPieces.isEmpty()) {
//            if (!argPathPieces.isEmpty()) {
//                return null;
//            }
//            JsonVoSupportParser parser = tune(new JsonVoSupportParser(new StringReader(requestBody)));
//            parser.setExpectedTopLevelItems(new ObjectInfoImpl(List.class).setCollectionItemsInfos(m.getParams()));
//            try {
//                parser.any();
//                if (parser.getParsedTopLevelObjects().isEmpty()) {
//                    return null;
//                } else if (parser.getParsedTopLevelObjects().size() != 1 || !(parser.getParsedTopLevelObjects().get(0) instanceof List)) {
//                    throw new RuntimeException("Cannot parse request body as " + m.getParams());
//                } else {
//                    return (List) parser.getParsedTopLevelObjects().get(0);
//                }
//            } catch (ParseException e) {
//                throw new RuntimeException("Cannot parse request body as " + m.getParams() + ":\n" + e, e);
//            }
//        } else if (m.isLastNotFlat()) {
//            if (m.getParams().size() != argPathPieces.size() + 1) {
//                return null;
//            }
//            List result = new ArrayList(argPathPieces.size() + 1);
//
//            for (int i = 0; i < argPathPieces.size(); i++) {
//                String argPathPiece = argPathPieces.get(i);
//                ObjectInfo pd = m.getParams().get(i);
//                try {
//                    result.add(pathArgParser.parsePathItem(pd, argPathPiece));
//                } catch (SwcArgParsingException e) {
//                    log.debug("Cannot parse {} as {}", argPathPiece, pd, e);
//                    return null;
//                }
//            }
//
//            Object lastArg = parseBody(m.getParams().get(m.getParams().size() - 1), requestBody);
//            result.add(lastArg);
//            return result;
//        } else {
//            if (m.getParams().size() != argPathPieces.size()) {
//                return null;
//            }
//            List result = new ArrayList(argPathPieces.size());
//            for (int i = 0; i < argPathPieces.size(); i++) {
//                String argPathPiece = argPathPieces.get(i);
//                ObjectInfo pd = m.getParams().get(i);
//                try {
//                    result.add(pathArgParser.parsePathItem(pd, argPathPiece));
//                } catch (SwcArgParsingException e) {
//                    log.debug("Cannot parse {} as {}", argPathPiece, pd, e);
//                    return null;
//                }
//            }
//            return result;
//        }
//    }
//
//    private List<Object> parseBody(List<ObjectInfo> pds, String requestBody) {
//        if (requestBody == null || requestBody.isEmpty()) {
//            return Collections.emptyList();
//        }
//        JsonVoSupportParser parser = tune(new JsonVoSupportParser(new StringReader(requestBody)));
//        parser.setExpectedTopLevelItems(pds);
//        try {
//            parser.any();
//            return parser.getParsedTopLevelObjects();
//        } catch (ParseException e) {
//            throw new RuntimeException("Cannot parse request body as " + pds + " due to:\n" + e, e);
//        }
//    }
//
//    private JsonVoSupportParser tune(JsonVoSupportParser parser) {
//        parser.setStrictExpectedObjects(true);
//        parser.setReflectionProvider(refProvider);
////        parser.setIdFactory(PolyglotReflectionConst.ID_FACTORY);
////        parser.setIdParser(PolyglotReflectionConst.ID_PARSER);
//        return parser;
//    }
//
//    private Object parseBody(ObjectInfo pd, String requestBody) {
//        JsonVoSupportParser parser = tune(new JsonVoSupportParser(new StringReader(requestBody)));
//        parser.setExpectedTopLevelItems(Collections.singletonList(pd));
//        try {
//            parser.any();
//            List l = parser.getParsedTopLevelObjects();
//            if (l.isEmpty()) {
//                return null;
//            }
//            return l.get(0);
//        } catch (ParseException e) {
//            throw new RuntimeException("Cannot parse request body as " + pd + " due to:\n" + e, e);
//        }
//    }

}
