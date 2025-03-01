package mpc.html;

import lombok.SneakyThrows;
import mpc.exception.RequiredRuntimeException;
import mpu.X;
import mpu.core.ARG;
import org.apache.tools.ant.util.ReaderInputStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

//https://habr.com/ru/articles/753332/
public class UXPath {

	@SneakyThrows
	public static void main(String[] args) {
//		X.exit(stringToDocument("<xml></xml>"));
		;
//		FileInputStream fileIS = new FileInputStream("<xml><xml>1</xml>2</xml>");
//		String expression = "string(//xml/count())";
//		String expression = "/*";
		String expression = "/xml/t/text()";
//		String data = "<xml><tag>1</tag><tag>11</tag>2</xml>";
		String data = "<xml><tag>12</tag><tag>13</tag><t>2</t></xml>";
//		X.exit(parseTagValues(data, expression));
		X.exit(parseString(data, expression));
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
//		Document xmlDocument = builder.parse(new StringInputStream(data));
		Document xmlDocument = builder.parse(new ByteArrayInputStream(data.getBytes()));
		XPath xPath = XPathFactory.newInstance().newXPath();
//		String expression = "/Tutorials/Tutorial";
		NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);
		X.exit(nodeList.item(1).getNodeValue());
	}

	@SneakyThrows
	public static Node parseNodeValue(String xmlData, String xPath, Node... defRq) {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		Document xmlDocument = builder.parse(new ReaderInputStream(new StringReader(xmlData)));
		XPath xPath0 = XPathFactory.newInstance().newXPath();
		NodeList nodeList = (NodeList) xPath0.compile(xPath).evaluate(xmlDocument, XPathConstants.NODESET);
		if (nodeList.getLength() == 1) {
			return nodeList.item(0);
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Except single node by xPath [%s], but cam '%s' nodes from data: %s", xPath, nodeList.getLength(), xmlData), defRq);
	}

	@SneakyThrows
	public static List<Node> parseNodeValues(String xmlData, String xPath, List<Node>... defRq) {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		Document xmlDocument = builder.parse(new ReaderInputStream(new StringReader(xmlData)));
		XPath xPath0 = XPathFactory.newInstance().newXPath();
		NodeList nodeList = (NodeList) xPath0.compile(xPath).evaluate(xmlDocument, XPathConstants.NODESET);
		if (nodeList.getLength() > 0) {
			LinkedList<Node> l = new LinkedList();
			for (int i = 0; i < nodeList.getLength(); i++) {
				l.add(nodeList.item(i));
			}
			return l;
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Except nodes by xPath [%s], but cam '%s' nodes from data: %s", xPath, nodeList.getLength(), xmlData), defRq);
	}

	@SneakyThrows
	public static String parseString(String xmlData, String xPath, String... defRq) {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		Document xmlDocument = builder.parse(new ReaderInputStream(new StringReader(xmlData)));
		XPath xPath0 = XPathFactory.newInstance().newXPath();
		String data = (String) xPath0.compile(xPath).evaluate(xmlDocument, XPathConstants.STRING);
		if (X.notEmpty(data)) {
			return data;
		}
		return ARG.toDefThrow(() -> new RequiredRuntimeException("Except string by xPath [%s], but cam empty from data: %s", xPath, xmlData), defRq);
	}

	//
	//
	//
	@SneakyThrows
	public static Document stringToDocument(CharSequence xmlData, Document... defRq) {
		try {
			return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(xmlData.toString().getBytes()));
		} catch (Exception ex) {
			return ARG.toDefThrow(() -> new RequiredRuntimeException(ex, "Error parse xml: %s", xmlData), defRq);
		}
	}

	public static String getNodeTagValue(Element element, String tagName) {
		NodeList list = element.getElementsByTagName(tagName);
		if (list != null && list.getLength() > 0) {
			NodeList subList = list.item(0).getChildNodes();
			if (subList != null && subList.getLength() > 0) {
				return subList.item(0).getNodeValue();
			}
		}
		return null;
	}



	public static Map<String, String> getNodeTagMapValues(Element element) {
		Map<String, String> resultMap = new LinkedHashMap<>();
		NodeList childNodes = element.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node node = childNodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element childElement = (Element) node;
				resultMap.put(childElement.getTagName(), childElement.getTextContent());
			}
		}
		return resultMap;
	}

	//
	//
	//

	public static class XmlContentExtractor {

		public static String getXmlContent(String xmlInput) {
			try {
				// Создаем экземпляр DocumentBuilderFactory
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();

				// Парсим входную строку XML
				Document document = builder.parse(new java.io.ByteArrayInputStream(xmlInput.getBytes()));

				// Создаем XPath для извлечения содержимого тега <xml>
				XPathFactory xpathFactory = XPathFactory.newInstance();
				XPath xpath = xpathFactory.newXPath();
				Node xmlNode = (Node) xpath.evaluate("/xml", document, XPathConstants.NODE);

				// Проверяем, что узел не пустой
				if (xmlNode != null) {
					// Создаем трансформер для преобразования узла в строку
					TransformerFactory transformerFactory = TransformerFactory.newInstance();
					Transformer transformer = transformerFactory.newTransformer();
					transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
					transformer.setOutputProperty(OutputKeys.INDENT, "yes");

					// Преобразуем узел в строку
					java.io.StringWriter writer = new java.io.StringWriter();
					transformer.transform(new DOMSource(xmlNode), new StreamResult(writer));

					return writer.getBuffer().toString(); // Возвращаем строку с тегами
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null; // Или можно выбросить исключение
		}

		public static void main(String[] args) {
			String xmlInput = "<xml><tag>12</tag><tag>13</tag>14</xml>";
			String content = getXmlContent(xmlInput);
			System.out.println(content); // Вывод: <tag>12</tag><tag>13</tag><t>14</t>
		}
	}

}
