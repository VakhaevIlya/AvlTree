import java.util.*;


public class AvlTree <T extends Comparable<T>> implements Set<T> {


    public AvlTree() {
        this.comparator = null;
    }

    private class Node<T> { //узел дерева
        private T key;
        private int h; //высота поддерева с корнем в данном узле
        public Node<T> lt, rt, parent;  // левые правые поддеревья(значения ключей в узлах) и родительский узел

        public Node(T key, Node<T> parent) {
            this.key = key;
            this.h = 1; //высота поддерева с корнем в данном узле
            this.lt = null;
            this.rt = null; //левый и правый узел
            this.parent = parent;

        }

        public int height() { //подсчет высоты поддеревьев
            if (lt == null && rt == null)
                return 0;
            else if (lt == null)
                return rt.h;
            else if (rt == null)
                return lt.h;
            else
                return 1 + Math.max(lt.h, rt.h);
        }
        public int balanceFactor() { //баланс фактор данного узла, если он равен 0, то дерево сбалансировано
            if (lt == null && rt == null)
                return 0; //разницы между уровнями узлов нет
            else if (lt == null)
                return rt.h; //перевес вправо (+h) (rt.h - null = rt.h)
            else if (rt == null)
                return -lt.h; //перевес влево (-h) (null - lt.h = -lt.h)
            else
                return rt.h - lt.h;
        }


    }

    private Node root;
    public int size = 0; //кол-во узлов в дереве
    private final Comparator<T> comparator;

    @Override
    public int size() {
        return size;
    }


    //В данном методе проверяется существует ли дерево, если нет то возвращаем True, иначе False
    @Override
    public boolean isEmpty() {
        return root == null;
    }

    @Override // Метод проверяющий наличие узла в дереве
    public boolean contains(Object o) {
        if (root == null) {
            return false;
        }
        T findKey = (T) o;
        Node<T> findNode = find(findKey, (Node<T>) root);
        return findNode != null && findKey.compareTo(findNode.key) == 0; // возвращает true, если искомый ключ есть и найден
    }
// Метод в котором ищется узел, содержащий искомый ключ.
    private Node<T> find(T findKey, Node<T> node) {
        if (node == null) {
            return null;
        }
        int comparison = findKey.compareTo(node.key); // Переменная сравнения искомого ключа с ключом узла.
         // Если они равны возващает текущий узел.
        if (comparison == 0)  {
            return node; }
        // Искомый элемент больше, слдовательно идем в правое поддерево
        else if (comparison > 0) {
            return find(findKey, node.rt); }
        // Искомый элемент меньше, слдовательно идем в левое поддерево
        else {
            return find(findKey, node.lt);
        }
    }

    public boolean add(T key) {
        // Если корень пуст, создаем его с введенным ключевым значением
        if (root == null)
            root = new Node(key, null);
        else {
            Node node = root;
            Node parent;
            //пока не нашли пустой узел
            while (true) {
                // Переменная, в которой сравниваются добавляемый ключ и текущий клч узла.
                int cmp = key.compareTo((T) node.key);
                //если добавляемый узел равен по ключу текущему узлу, не добавляем узел с этим ключом
                if (cmp == 0)
                    return false;

                parent = node;

                if(cmp < 0){
                    node=node.lt;
                }
                else {
                    node=node.rt;
                }
                // Входим в это условие, когда найдем пустой узел. Вставляем узел в левое или правое поддерево.
                if (node == null) {
                    if (cmp < 0) {
                        parent.lt = new Node(key, parent);
                    } else { //если cmp >=, тк в правое поддерево можно добавлять одинаковые элементы
                        parent.rt = new Node(key, parent);
                    }
                    rebalance(parent);
                    break;
                }
            }
        }
        size++;
        return true;
    }



//итератор для перебора коллекции ArrayList,
    @Override
    public Iterator<T> iterator() {
        ArrayList<T> list = new ArrayList<>(size);
        inorderTraverse(root, list);
        return list.iterator(); //Возвращает итератор для обращения к элементам списка
    }

//конвертирует набор элементов в массив
    @Override
    public Object[] toArray() {
        List<T> list = new ArrayList<>(size);
        inorderTraverse(root, list);
        return list.toArray(); //возвращает массив элементов
    }

    //Создаёт список с элементами дереве в возрастающем порядке, current - текущий элемент
    private void inorderTraverse(Node current, List<T> list) {
        if (current == null) {
            return;
        }
        inorderTraverse(current.lt, list);
        list.add((T) current.key);
        inorderTraverse(current.rt, list);
    }

    //возвращает список
    public List<T> inorderTraverse() {
        List<T> list = new ArrayList<T>(size);
        inorderTraverse(root, list);
        return list;
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return null;
    }



    private void rebalance(Node node) {
    node.height();
        int balanceFactor = node.balanceFactor();
        if (balanceFactor == -2) {
            if (node.lt.lt.height() >= node.lt.rt.height())
                node = rotateRight(node);
            else
                node = rotateLeftThenRight(node);

        } else if (balanceFactor == 2) {
            if (node.rt.rt.height() >= node.rt.lt.height())
                node = rotateLeft(node);
            else
                node = rotateRightThenLeft(node);
        }

        if (node.parent != null) {
            rebalance(node.parent);
        } else {
            root = node; //перезаписываем корневой узел
        }
    }

    private Node rotateRight(Node a) {

        Node b = a.lt;

        b.parent = a.parent; //родителю b присваивается значение родителя a

        a.lt = b.rt;

        if (a.lt != null)
            a.lt.parent = a;

        //правым сыном b становится a
        b.rt = a;
        a.parent = b; //родителем а становится b

        if (b.parent != null) {
            if (b.parent.rt == a) {
                b.parent.rt = b;
            } else {
                b.parent.lt = b;
            }
        }


        return b;
    }

    private Node rotateLeft(Node a) {

        Node b = a.rt;
        b.parent = a.parent;

        a.rt = b.lt;

        if (a.rt != null)
            a.rt.parent = a;

        b.lt = a;
        a.parent = b;

        if (b.parent != null) {
            if (b.parent.rt == a) {
                b.parent.rt = b;
            } else {
                b.parent.lt = b;
            }
        }
        return b;
    }

    //большой левый поворот
    private Node rotateLeftThenRight(Node node) {
        node.lt = rotateLeft(node.lt);
        return rotateRight(node);
    }

    //большой правый поворот
    private Node rotateRightThenLeft(Node node) {
        node.rt = rotateRight(node.rt);
        return rotateLeft(node);
    }


    //Удаление объекта из дерева. Если объект найден(true), то он удаляется
    private boolean remove(T key) {
        if (root == null)
            return false;
        Node node = root;
        Node parent = root;
        Node delNode = null;
        Node child = root;

        // ищем удаляемый элемент и Если дочерний узел есть
        while (child != null) {
            // идем вниз
            parent = node;
            node = child;
            //сравниваем тот что надо удалить с текущим
            int cmp = key.compareTo((T) node.key);
            // если ключ удаляемого узла больше ключа текущего, идем вправо, иначе идем влево
            if(cmp >= 0)  {
                child=node.rt;
            }
            else {
                child = node.lt;
            }

            if (cmp == 0)
                delNode = node; // Если значения ключей совпали, то переходим к входим в условие
        }

        // если такой элемент есть, то удаляем его и возвращаем true
        if (delNode != null) {
            // присваиваем удаляемому узлу ключ текущего (перепривязка)
            delNode.key = node.key;

            if(node.lt != null)
            {
                child = node.lt;
            }
            else
            {
                child =node.rt;
            }
            //если ключи корневого узла и удаляемого равны
            int cmpre = key.compareTo((T) root.key);
            if (cmpre == 0) {
                root = child;
            } else {
                if (parent.lt == node) {
                    parent.lt = child;
                } else {
                    parent.rt = child;
                }
                rebalance(parent);
            }
            size--;
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(Object o) {
        //присваиваем элементу key значение объекта о
        T key = (T) o;
        return remove(key);
    }

//true, если все элементы C содержатся в коллекции
    @Override
    public boolean containsAll(Collection<?> c) {
        Object[] arr = c.toArray();
        for (Object item : arr) { //берется один элемент C и проверяется его принадлежность коллекции
            if (!contains(item))
                return false;
        }
        return true;
    }

    //добавление всех элементов коллекции Т, при условии если их ещё нет
    @Override
    public boolean addAll(Collection<? extends T> c) {
        Object[] arr = c.toArray();
        int cTrue = 0;
        for (Object item : arr) {
            if (add((T) item))
                cTrue += 1;
        }
        return cTrue != 0;
    }

    //удаляет элементы, не принадлежащие переданной коллекции (возвращает новый размер дерева)
    @Override
    public boolean retainAll(Collection<?> c) {
        Objects.requireNonNull(c); //Проверка, что указанная ссылка объекта не null
        List<T> tree = inorderTraverse();
        int oldSize = tree.size(); //размер списка
        tree.retainAll(c); //передаём список в коллекцию С
        int newSize = tree.size(); //размер коллекции, включая переданный

        //очищаем дерево
        root = null;
        size = 0;

        //Добавляем элементы в дерево
        addAll(tree);

        int result = newSize - oldSize; //размер переданной коллекции (сколько элементов принадлежащих коллекции)

        return result != 0;
    }

    @Override //удаляет элементы, принадлежащие переданной коллекции
    public boolean removeAll(Collection<?> c) {
        Objects.requireNonNull(c);
        Object[] arr = c.toArray();
        int cTrue = 0;
        for (Object item : arr) {
            if (remove(item))
                cTrue += 1;
        }
        return cTrue != 0;
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

}