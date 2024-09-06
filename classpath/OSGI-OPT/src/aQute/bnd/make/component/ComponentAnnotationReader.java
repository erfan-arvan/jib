package aQute.bnd.make.component;

import static aQute.bnd.osgi.Constants.COMPONENT_ACTIVATE;
import static aQute.bnd.osgi.Constants.COMPONENT_CONFIGURATION_POLICY;
import static aQute.bnd.osgi.Constants.COMPONENT_DEACTIVATE;
import static aQute.bnd.osgi.Constants.COMPONENT_DESCRIPTORS;
import static aQute.bnd.osgi.Constants.COMPONENT_DESIGNATE;
import static aQute.bnd.osgi.Constants.COMPONENT_DESIGNATEFACTORY;
import static aQute.bnd.osgi.Constants.COMPONENT_DYNAMIC;
import static aQute.bnd.osgi.Constants.COMPONENT_ENABLED;
import static aQute.bnd.osgi.Constants.COMPONENT_FACTORY;
import static aQute.bnd.osgi.Constants.COMPONENT_IMMEDIATE;
import static aQute.bnd.osgi.Constants.COMPONENT_IMPLEMENTATION;
import static aQute.bnd.osgi.Constants.COMPONENT_MODIFIED;
import static aQute.bnd.osgi.Constants.COMPONENT_MULTIPLE;
import static aQute.bnd.osgi.Constants.COMPONENT_NAME;
import static aQute.bnd.osgi.Constants.COMPONENT_OPTIONAL;
import static aQute.bnd.osgi.Constants.COMPONENT_PROPERTIES;
import static aQute.bnd.osgi.Constants.COMPONENT_PROVIDE;
import static aQute.bnd.osgi.Constants.COMPONENT_SERVICEFACTORY;
import static aQute.bnd.osgi.Constants.COMPONENT_VERSION;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;
import aQute.bnd.component.error.DeclarativeServicesAnnotationError;
import aQute.bnd.component.error.DeclarativeServicesAnnotationError.ErrorType;
import aQute.bnd.osgi.Annotation;
import aQute.bnd.osgi.ClassDataCollector;
import aQute.bnd.osgi.Clazz;
import aQute.bnd.osgi.Clazz.FieldDef;
import aQute.bnd.osgi.Clazz.MethodDef;
import aQute.bnd.osgi.Descriptors;
import aQute.bnd.osgi.Descriptors.TypeRef;
import aQute.bnd.osgi.Processor;
import aQute.bnd.osgi.Verifier;
import aQute.service.reporter.Reporter;

/**
 * This converts bnd style annotations to, roughly, the header format.
 */
public class ComponentAnnotationReader extends ClassDataCollector {
	private final static Logger								logger					= LoggerFactory
			.getLogger(ComponentAnnotationReader.class);

	String													EMPTY[]					= new String[0];
	private static final String								V1_1					= "1.1.0";															// "1.1.0"
	static Pattern											BINDDESCRIPTOR			= Pattern
			.compile("\\(L([^;]*);(Ljava/util/Map;|Lorg/osgi/framework/ServiceReference;)*\\)V");
	static Pattern											BINDMETHOD				= Pattern
			.compile("(set|bind|add)(.)(.*)");

	static Pattern											ACTIVATEDESCRIPTOR		= Pattern.compile(
			"\\(((Lorg/osgi/service/component/ComponentContext;)|(Lorg/osgi/framework/BundleContext;)|(Ljava/util/Map;))*\\)V");
	static Pattern											OLDACTIVATEDESCRIPTOR	= Pattern
			.compile("\\(Lorg/osgi/service/component/ComponentContext;\\)V");

	static Pattern											OLDBINDDESCRIPTOR		= Pattern
			.compile("\\(L([^;]*);\\)V");
	static Pattern											REFERENCEBINDDESCRIPTOR	= Pattern
			.compile("\\(Lorg/osgi/framework/ServiceReference;\\)V");

	static String[]											ACTIVATE_ARGUMENTS		= {
																							"org.osgi.service.component.ComponentContext",
																							"org.osgi.framework.BundleContext",
																							Map.class.getName(),
																							"org.osgi.framework.BundleContext"
																						};
	static String[]											OLD_ACTIVATE_ARGUMENTS	= {
																							"org.osgi.service.component.ComponentContext"
																						};

	Reporter												reporter				= new Processor();
	MethodDef												method;
	FieldDef												field;
	TypeRef													className;
	Clazz													clazz;
	TypeRef													interfaces[];
	Set<String>												multiple				= new HashSet<String>();
	Set<String>												optional				= new HashSet<String>();
	Set<String>												dynamic					= new HashSet<String>();

	Map<String,String>										map						= new TreeMap<String,String>();
	Set<String>												descriptors				= new HashSet<String>();
	List<String>											properties				= new ArrayList<String>();
	String													version					= null;
	Map<String,List<DeclarativeServicesAnnotationError>>	mismatchedAnnotations	= new HashMap<String,List<DeclarativeServicesAnnotationError>>();

	// TODO make patterns for descriptors

	ComponentAnnotationReader(Clazz clazz) {
		this.clazz = clazz;
	}

	public void setReporter(Reporter reporter) {
		this.reporter = reporter;
	}

	public Reporter getReporter() {
		return this.reporter;
	}

	public static Map<String,String> getDefinition(Clazz c) throws Exception {
		try (Processor p = new Processor()) {
			return getDefinition(c, p);
		}
	}

	public static Map<String,String> getDefinition(Clazz c, Reporter reporter) throws Exception {
		ComponentAnnotationReader r = new ComponentAnnotationReader(c);
		r.setReporter(reporter);
		c.parseClassFileWithCollector(r);
		r.finish();
		return r.map;
	}

	@Override
	public void annotation(Annotation annotation) {
		String fqn = annotation.getName().getFQN();

		if (fqn.equals("aQute.bnd.annotation.component.Component")) {
			if (!mismatchedAnnotations.isEmpty()) {
				String componentName = annotation.get(Component.NAME);
				componentName = (componentName == null) ? className.getFQN() : componentName;
				for (Entry<String,List<DeclarativeServicesAnnotationError>> e : mismatchedAnnotations.entrySet()) {
					for (DeclarativeServicesAnnotationError errorDetails : e.getValue()) {
						if (errorDetails.fieldName != null) {
							reporter.error(
									"The DS component %s uses bnd annotations to declare it as a component, but also uses the standard DS annotation: %s on field %s. It is an error to mix these two types of annotations",
									componentName, e.getKey(), errorDetails.fieldName).details(errorDetails);
						} else if (errorDetails.methodName != null) {
							reporter.error(
									"The DS component %s uses bnd annotations to declare it as a component, but also uses the standard DS annotation: %s on method %s with signature %s. It is an error to mix these two types of annotations",
									componentName, e.getKey(), errorDetails.methodName, errorDetails.methodSignature)
									.details(errorDetails);
						} else {
							reporter.error(
									"The DS component %s uses bnd annotations to declare it as a component, but also uses the standard DS annotation: %s. It is an error to mix these two types of annotations",
									componentName, e.getKey()).details(errorDetails);
						}
					}
				}
				return;
			}

			set(COMPONENT_NAME, annotation.get(Component.NAME), "<>");
			set(COMPONENT_FACTORY, annotation.get(Component.FACTORY), false);
			setBoolean(COMPONENT_ENABLED, annotation.get(Component.ENABLED), true);
			setBoolean(COMPONENT_IMMEDIATE, annotation.get(Component.IMMEDIATE), false);
			setBoolean(COMPONENT_SERVICEFACTORY, annotation.get(Component.SERVICEFACTORY), false);

			if (annotation.get(Component.DESIGNATE) != null) {
				TypeRef configs = annotation.get(Component.DESIGNATE);
				if (configs != null) {
					set(COMPONENT_DESIGNATE, configs.getFQN(), "");
				}
			}

			if (annotation.get(Component.DESIGNATE_FACTORY) != null) {
				TypeRef configs = annotation.get(Component.DESIGNATE_FACTORY);
				if (configs != null) {
					set(COMPONENT_DESIGNATEFACTORY, configs.getFQN(), "");
				}
			}

			setVersion((String) annotation.get(Component.VERSION));

			String configurationPolicy = annotation.get(Component.CONFIGURATION_POLICY);
			if (configurationPolicy != null)
				set(COMPONENT_CONFIGURATION_POLICY, configurationPolicy.toLowerCase(), "");

			doProperties(annotation);

			Object[] provides = (Object[]) annotation.get(Component.PROVIDE);
			String[] p;
			if (provides == null) {
				// Use the found interfaces, but convert from internal to
				// fqn.
				if (interfaces != null) {
					List<String> result = new ArrayList<String>();
					for (int i = 0; i < interfaces.length; i++) {
						if (!interfaces[i].getBinary().equals("scala/ScalaObject"))
							result.add(interfaces[i].getFQN());
					}
					p = result.toArray(EMPTY);
				} else
					p = EMPTY;
			} else {
				// We have explicit interfaces set
				p = new String[provides.length];
				for (int i = 0; i < provides.length; i++) {
					p[i] = ((TypeRef) provides[i]).getFQN();
				}
			}
			if (p.length > 0) {
				set(COMPONENT_PROVIDE, Processor.join(Arrays.asList(p)), "<>");
			}

		} else if (fqn.equals("aQute.bnd.annotation.component.Activate")) {
			if (!checkMethod())
				setVersion(V1_1);

			// TODO ... use the new descriptor to do better check

			if (!ACTIVATEDESCRIPTOR.matcher(method.getDescriptor().toString()).matches())
				reporter.error(
						"Activate method for %s does not have an acceptable prototype, only Map, ComponentContext, or BundleContext is allowed. Found: %s",
						className, method.getDescriptor())
						.details(new DeclarativeServicesAnnotationError(className.getFQN(), method.getName(),
								method.getDescriptor().toString(), ErrorType.ACTIVATE_SIGNATURE_ERROR));

			if (method.getName().equals("activate")
					&& OLDACTIVATEDESCRIPTOR.matcher(method.getDescriptor().toString()).matches()) {
				// this is the default!
			} else {
				setVersion(V1_1);
				set(COMPONENT_ACTIVATE, method, "<>");
			}

		} else if (fqn.equals("aQute.bnd.annotation.component.Deactivate")) {
			if (!checkMethod())
				setVersion(V1_1);

			if (!ACTIVATEDESCRIPTOR.matcher(method.getDescriptor().toString()).matches())
				reporter.error(
						"Deactivate method for %s does not have an acceptable prototype, only Map, ComponentContext, or BundleContext is allowed. Found: %s",
						className, method.getDescriptor())
						.details(new DeclarativeServicesAnnotationError(className.getFQN(), method.getName(),
								method.getDescriptor().toString(), ErrorType.DEACTIVATE_SIGNATURE_ERROR));
			if (method.getName().equals("deactivate")
					&& OLDACTIVATEDESCRIPTOR.matcher(method.getDescriptor().toString()).matches()) {
				// This is the default!
			} else {
				setVersion(V1_1);
				set(COMPONENT_DEACTIVATE, method, "<>");
			}
		} else if (fqn.equals("aQute.bnd.annotation.component.Modified")) {
			if (!ACTIVATEDESCRIPTOR.matcher(method.getDescriptor().toString()).matches())
				reporter.error(
						"Modified method for %s does not have an acceptable prototype, only Map, ComponentContext, or BundleContext is allowed. Found: %s",
						className, method.getDescriptor())
						.details(new DeclarativeServicesAnnotationError(className.getFQN(), method.getName(),
								method.getDescriptor().toString(), ErrorType.MODIFIED_SIGNATURE_ERROR));
			set(COMPONENT_MODIFIED, method, "<>");
			setVersion(V1_1);
		} else if (fqn.equals("aQute.bnd.annotation.component.Reference")) {

			String name = (String) annotation.get("aQute.bnd.annotation.component.Reference");
			String bind = method.getName();
			String unbind = null;

			if (name == null) {
				Matcher m = BINDMETHOD.matcher(method.getName());
				if (m.matches()) {
					name = m.group(2).toLowerCase() + m.group(3);
				} else {
					name = method.getName().toLowerCase();
				}
			}
			String simpleName = name;

			unbind = annotation.get(Reference.UNBIND);

			// this error reporting currently handled in HeaderReader. If we
			// rewrite this to go directly to ComponentDesc, we'll want this.
			// if (unbind != null && !descriptors.contains(unbind))
			// reporter.error("In component %s, for bind method %s, missing
			// unbind method %s", name, bind, unbind);

			if (bind != null) {
				name = name + "/" + bind;
				if (unbind != null)
					name = name + "/" + unbind;
			}
			String service = null;
			TypeRef serviceTR = annotation.get(Reference.SERVICE);
			if (serviceTR != null)
				service = serviceTR.getFQN();

			if (service == null) {
				// We have to find the type of the current method to
				// link it to the referenced service.
				Matcher m = BINDDESCRIPTOR.matcher(method.getDescriptor().toString());
				if (m.matches()) {
					service = Descriptors.binaryToFQN(m.group(1));
				} else
					throw new IllegalArgumentException(
							"Cannot detect the type of a Component Reference from the descriptor: "
									+ method.getDescriptor());
			}

			// Check if we have a target, this must be a filter
			String target = annotation.get(Reference.TARGET);
			if (target != null) {
				String error = Verifier.validateFilter(target);
				if (error != null) {
					reporter.error("Invalid target filter %s for %s: %s", target, name, error)
							.details(new DeclarativeServicesAnnotationError(className.getFQN(), bind,
									method.getDescriptor().toString(), ErrorType.INVALID_TARGET_FILTER));
				}
				service = service + target;
			}

			Integer c = annotation.get(Reference.TYPE);
			if (c != null && !c.equals(0) && !c.equals((int) '1')) {
				service = service + (char) c.intValue();
			}

			if (map.containsKey(name))
				reporter.error(
						"In component %s, Multiple references with the same name: %s. Previous def: %s, this def: %s",
						name, map.get(name), service, "")
						.details(new DeclarativeServicesAnnotationError(className.getFQN(), null, null,
								ErrorType.MULTIPLE_REFERENCES_SAME_NAME));
			map.put(name, service);

			if (isTrue(annotation.get(Reference.MULTIPLE)))
				multiple.add(simpleName);
			if (isTrue(annotation.get(Reference.OPTIONAL)))
				optional.add(simpleName);
			if (isTrue(annotation.get(Reference.DYNAMIC)))
				dynamic.add(simpleName);

			if (!checkMethod())
				setVersion(V1_1);
			else if (REFERENCEBINDDESCRIPTOR.matcher(method.getDescriptor().toString()).matches()
					|| !OLDBINDDESCRIPTOR.matcher(method.getDescriptor().toString()).matches())
				setVersion(V1_1);
		} else if (fqn.startsWith("org.osgi.service.component.annotations")) {
			DeclarativeServicesAnnotationError errorDetails;

			switch (annotation.getElementType()) {
				case METHOD :
					errorDetails = new DeclarativeServicesAnnotationError(className.getFQN(), method.getName(),
							method.getDescriptor().toString(), ErrorType.MIXED_USE_OF_DS_ANNOTATIONS_BND);
					break;
				case FIELD :
					errorDetails = new DeclarativeServicesAnnotationError(className.getFQN(), field.getName(),
							ErrorType.MIXED_USE_OF_DS_ANNOTATIONS_BND);
					break;
				default :
					errorDetails = new DeclarativeServicesAnnotationError(className.getFQN(), null,
							ErrorType.MIXED_USE_OF_DS_ANNOTATIONS_BND);
			}

			List<DeclarativeServicesAnnotationError> errors = mismatchedAnnotations.get(fqn);
			if (errors == null) {
				errors = new ArrayList<DeclarativeServicesAnnotationError>();
				mismatchedAnnotations.put(fqn, errors);
			}
			errors.add(errorDetails);
		}
	}

	private void setVersion(String v) {
		if (v == null)
			return;

		if (version == null)
			version = v;
		else if (v.compareTo(version) > 0) // we're safe to 9.9.9
			version = v;
	}

	private boolean checkMethod() {
		return Modifier.isPublic(method.getAccess()) || Modifier.isProtected(method.getAccess());
	}

	static Pattern PROPERTY_PATTERN = Pattern.compile("\\s*([^=\\s]+)\\s*=(.+)");

	private void doProperties(aQute.bnd.osgi.Annotation annotation) {
		Object[] properties = annotation.get(Component.PROPERTIES);

		if (properties != null) {
			for (Object o : properties) {
				String p = (String) o;
				Matcher m = PROPERTY_PATTERN.matcher(p);
				if (m.matches())
					this.properties.add(m.group(1) + "=" + m.group(2));
				else
					throw new IllegalArgumentException(
							"Malformed property '" + p + "' on: " + annotation.get(Component.NAME));
			}
		}
	}

	private boolean isTrue(Object object) {
		if (object == null)
			return false;
		return (Boolean) object;
	}

	private void setBoolean(String string, Object object, boolean b) {
		if (object == null)
			object = b;

		Boolean bb = (Boolean) object;
		if (bb == b)
			return;

		map.put(string, bb.toString());
	}

	private void set(String string, Object object, Object deflt) {
		if (object == null || object.equals(deflt))
			return;

		map.put(string, object.toString());
	}

	/**
	 * Skip L and ; and replace / for . in an object descriptor. A string like
	 * Lcom/acme/Foo; becomes com.acme.Foo
	 * 
	 */

	// private String descriptorToFQN(String string) {
	// StringBuilder sb = new StringBuilder();
	// for (int i = 1; i < string.length() - 1; i++) {
	// char c = string.charAt(i);
	// if (c == '/')
	// c = '.';
	// sb.append(c);
	// }
	// return sb.toString();
	// }

	@Override
	public void classBegin(int access, TypeRef name) {
		className = name;
	}

	@Override
	public void implementsInterfaces(TypeRef[] interfaces) {
		this.interfaces = interfaces;
	}

	@Override
	public void method(Clazz.MethodDef method) {
		this.method = method;
		descriptors.add(method.getName());
	}

	@Override
	public void field(FieldDef field) {
		this.field = field;
	}

	void set(String name, Collection<String> l) {
		if (l.size() == 0)
			return;

		set(name, Processor.join(l), "<>");
	}

	public void finish() {
		set(COMPONENT_MULTIPLE, multiple);
		set(COMPONENT_DYNAMIC, dynamic);
		set(COMPONENT_OPTIONAL, optional);
		set(COMPONENT_IMPLEMENTATION, clazz.getFQN(), "<>");
		set(COMPONENT_PROPERTIES, properties);
		if (version != null) {
			set(COMPONENT_VERSION, version, "<>");
			logger.debug("Component {} is v1.1", map);
		}
		set(COMPONENT_DESCRIPTORS, descriptors);
	}

}
