package com.bkav.command.struct;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class WordTrieNode<T> {

	public WordTrieNode(WordTrieNode<T> parent, T id, String label) {
		this.label = label;
		this.parent = parent;
		this.id = id;
		this.childrens = new HashMap<>();
	}

	public WordTrieNode(WordTrieNode<T> parent, T id) {
		this(parent, id, null);
	}

	public WordTrieNode(WordTrieNode<T> parent) {
		this(parent, null, null);
	}
	
	public WordTrieNode() {
		this(null, null, null);
	}

	public WordTrieNode<T> getParent() {
		return this.parent;
	}

	public T getId() {
		return this.id;
	}

	public void setId(T id) {
		this.id = id;
	}

	public boolean isRoot() {
		return this.parent != null;
	}

	public boolean isLeaf() {
		return this.childrens.isEmpty();
	}

	public String getLabel() {
		return this.label;
	}

	public String[] getLabels() {
		List<String> listString = new ArrayList<>();
		WordTrieNode<T> node = this;
		while (!node.isRoot()) {
			listString.add(node.getLabel());
			node = node.getParent();
		}
		int size = listString.size();
		String[] output = new String[size];
		for (int index = 0; index < size; index++) {
			output[index] = listString.get(size - 1 - index);
		}
		return output;
	}

	public void addPhrase(String[] words, T id) {
		if (words.length == 0)
			return;
		WordTrieNode<T> node = this;
		for (int index = 0; index < words.length; index++) {
			String word = words[index];
			WordTrieNode<T> childNode = node.getChildrens().get(word);
			if (childNode == null) {
				childNode = new WordTrieNode<>(node, null, word);
				node.getChildrens().put(word, childNode);
			}
			node = childNode;
		}
		node.setId(id);
	}

	public void addPhrase(Iterator<String> words, T id) {
		if (!words.hasNext())
			return;
		WordTrieNode<T> node = this;
		while (words.hasNext()) {
			String word = words.next();
			WordTrieNode<T> childNode = node.getChildrens().get(word);
			if (childNode == null) {
				childNode = new WordTrieNode<>(node, null, word);
				node.getChildrens().put(word, childNode);
			}
			node = childNode;
		}
		node.setId(id);
	}

	public Collection<T> findPharases(String[] words, Collection<T> foundPharases) {
		WordTrieNode<T> node = this;
		if (foundPharases == null) {
			foundPharases = new HashSet<>();
		}
		for (int index = 0; index < words.length;) {
			String word = words[index];
			WordTrieNode<T> childNode = node.getChildrens().get(word);
			if (childNode != null) {
				node = childNode;
				index++;
			} else {
				if (node.getId() != null) {
					foundPharases.add(node.getId());
				}
				if (node == this) {
					index++;
				} else {
					node = this;
				}
			}
		}
		if (node.getId() != null) {
			foundPharases.add(node.getId());
		}
		return foundPharases;
	}

	public ResultsProcess findPharases(ResultsProcess currentResult) {
		WordTrieNode<T> currentNode = this;
		String[] words = currentResult.remains();
		if (words.length == 0) {
			return currentResult;
		}
		ListStringWithMask wordsWithMark =  new ListStringWithMask(words);
		wordsWithMark.setConfig(MaskConfig.getDefaultConfig());
		List<Integer> indexs = new ArrayList<>();
		for (int index = 0; index < words.length;) {
			WordTrieNode<T> childNode = currentNode.getChildrens().get(words[index]);
			if (childNode != null) {
				currentNode = childNode;
				indexs.add(index);
				index++;
				continue;
			}
			if (currentNode.getId() != null) {
				currentResult.addValue(currentNode.getId());
				wordsWithMark.setMark(indexs);
			}	
			if (currentNode == this) {
				index++;
			} else {
				currentNode = this;
			}
			indexs.clear();
		}
		if (currentNode.getId() != null) {
			currentResult.addValue(currentNode.getId());
			wordsWithMark.setMark(indexs);
		}
		indexs.clear();
		currentResult.stringsMark().setMarkWithRelativeIndex(wordsWithMark.markIndexs());
		return currentResult;
	}
	
	public Collection<T> findPharases(String[] words) {
		return findPharases(words, null);
	}

	public Collection<T> findPharases(Iterator<String> words, Collection<T> foundPharases) {
		WordTrieNode<T> node = this;
		if (foundPharases == null) {
			foundPharases = new HashSet<>();
		}
		String word = null;
		if (words.hasNext()) {
			word = words.next();
		}
		while (words.hasNext()) {
			WordTrieNode<T> childNode = node.getChildrens().get(word);
			if (childNode != null) {
				node = childNode;
				word = words.next();
				continue;
			} 
			if (node.getId() != null) {
				foundPharases.add(node.getId());
			}
			if (node == this) {
				word = words.next();
			} else {
				node = this;
			}
		}
		if (node.getId() != null) {
			foundPharases.add(node.getId());
		}
		return foundPharases;
	}
	
	public Collection<T> findPharases(Iterator<String> words) {
		return this.findPharases(words, null);
	}

	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		writer.append(":[" + this.id + "]");
		if (this.childrens.isEmpty()) {
			return writer.toString();
		}
		writer.append("{");
		Iterator<String> iterator = this.childrens.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			WordTrieNode<T> itemNode = this.childrens.get(key);
			writer.append("{");
			writer.append(key);
			writer.append(itemNode.toString());
			writer.append("}");
			if (iterator.hasNext()) {
				writer.append(";");
			} else {
				break;
			}
		}
		writer.append("}");
		return writer.toString();
	}

	public Map<String, WordTrieNode<T>> getChildrens() {
		return this.childrens;
	}

	private String label;
	private T id;
	private WordTrieNode<T> parent;
	private final Map<String, WordTrieNode<T>> childrens;
}