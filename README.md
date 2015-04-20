Easily inject custom CSS to you application.

CSSInject doesn't provide any helpers to easily add custom styles directly to some component. It's just a very simplistic way of attaching additional CSS to the same UI where the CSSInject component is attached.

##Usage
Inject custom css into head tag:

```
CSSInject css = new CSSInject(getUI());
css.setStyles(".v-label {color:red !important;}");
```

Link custom css file:

```
ThemeResource themeStyles = new ThemeResource("../demo/extra.css");
css.addStyleSheet(themeStyles);
```

Also you can remove it:

```
css.removeStyleSheet(themeStyles);
```

##Build and Development

```./gradlew idea``` - Intelij IDEA project files generation

```./gradlew :demo:vaadinRun``` - run embedded jetty server

```./gradlew :addon:publish``` - build and publish to maven repository, see more into build.gradle publishing.repositories.maven

If you need debug client side, you can use GWT SuperDev mode, run
code server ```./gradlew :demo:vaadinSuperDevMode``` and server ```./gradlew :demo:vaadinRun```  