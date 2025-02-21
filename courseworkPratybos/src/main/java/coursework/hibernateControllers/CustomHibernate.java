package coursework.hibernateControllers;

import coursework.model.*;
import coursework.model.enums.PublicationStatus;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CustomHibernate extends GenericHibernate {
    public CustomHibernate(EntityManagerFactory entityManagerFactory) {
        super(entityManagerFactory);
    }

    public User getUserByCredentials(String username, String password) {
        User user = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<User> query = cb.createQuery(User.class);
            Root<User> root = query.from(User.class);

            query.select(root).where(cb.and(cb.equal(root.get("login"), username), cb.equal(root.get("password"), password)));
            Query q;
            q = entityManager.createQuery(query);
            user =  (User) q.getSingleResult();
//            entityManager.getTransaction().begin();
//            result = entityManager.find(entityClass, id);
//            entityManager.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (entityManager != null) entityManager.close();
        }
        return user;
    }

    public List<Publication> getAvailablePublications(User user) {
        List<Publication> publications = new ArrayList<>();
        try {
            entityManager = entityManagerFactory.createEntityManager();
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Publication> query = cb.createQuery(Publication.class);
            Root<Publication> root = query.from(Publication.class);

            query.select(root).where(cb.and(cb.equal(root.get("publicationStatus"), PublicationStatus.AVAILABLE), cb.notEqual(root.get("owner"), user)));
            Query q;
            q = entityManager.createQuery(query);
            publications = q.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (entityManager != null) entityManager.close();
        }
        return publications;
    }

    public void deleteComment(int id) {
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            // Находим комментарий по ID
            var comment = entityManager.find(Comment.class, id);
            if (comment == null) {
                throw new IllegalArgumentException("Comment not found for id: " + id);
            }

            // Если у комментария есть родитель, удаляем его из списка дочерних комментариев родителя
            if (comment.getParentComment() != null) {
                Comment parentComment = entityManager.find(Comment.class, comment.getParentComment().getId());
                parentComment.getReplies().remove(comment); // Удаляем из списка дочерних комментариев родителя
                entityManager.merge(parentComment); // Обновляем родительский комментарий
            }

            // Удаляем дочерние комментарии
            comment.getReplies().forEach(reply -> {
                entityManager.remove(reply); // Удаляем каждый дочерний комментарий вручную
            });
            comment.getReplies().clear(); // Очистка списка дочерних комментариев

            // Удаляем сам комментарий
            entityManager.remove(comment);

            // Завершаем транзакцию
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback(); // Откат транзакции в случае ошибки
            }
            e.printStackTrace();
        } finally {
            if (entityManager != null) entityManager.close(); // Закрытие EntityManager
        }
    }



    public Chat getChatByClientsId(int ownerId, int clientId) {
        Chat chat = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Chat> query = cb.createQuery(Chat.class);
            Root<Chat> root = query.from(Chat.class);

            // Найти объекты Client по их ID
            Client owner = entityManager.find(Client.class, ownerId);
            Client client = entityManager.find(Client.class, clientId);

            if (owner != null && client != null) {
                // Построить запрос
                query.select(root).where(cb.and(
                        cb.equal(root.get("owner"), owner),
                        cb.equal(root.get("client"), client)
                        )
                );

                Query q;
                q = entityManager.createQuery(query);
                chat = (Chat) q.getSingleResult();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (entityManager != null) entityManager.close();
        }
        return chat;
    }

    public List<Comment> getCommentsByChat(Chat chat) {
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Comment> query = cb.createQuery(Comment.class);
            Root<Comment> root = query.from(Comment.class);

            query.select(root).where(cb.equal(root.get("chat"), chat));

            List<Comment> comments = entityManager.createQuery(query).getResultList();
            entityManager.getTransaction().commit();

            return comments;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (entityManager != null) entityManager.close();
        }
        return Collections.emptyList();
    }

    public Chat getChatByPublication(int publicationId) {
        Chat chat = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Chat> query = cb.createQuery(Chat.class);
            Root<Chat> root = query.from(Chat.class);

            // Найти объекты Client по их ID
            Publication publication = entityManager.find(Publication.class, publicationId);

            if (publication != null) {
                // Построить запрос
                query.select(root).where(cb.equal(root.get("publication"), publication));

                Query q;
                q = entityManager.createQuery(query);
                chat = (Chat) q.getSingleResult();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (entityManager != null) entityManager.close();
        }
        return chat;
    }

    public List<Publication> getOwnPublications(User user) {

        List<Publication> publications = new ArrayList<>();
        try {
            entityManager = entityManagerFactory.createEntityManager();
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Publication> query = cb.createQuery(Publication.class);
            Root<Publication> root = query.from(Publication.class);

            query.select(root).where(cb.equal(root.get("owner"), user));
            //query.orderBy(cb.desc(root.get("requestDate")));

            Query q = entityManager.createQuery(query);
            publications = q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return publications;
    }

    public List<Publication> getBorrowedPublications(User user) {

        List<Publication> publications = new ArrayList<>();
        try {
            entityManager = entityManagerFactory.createEntityManager();
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Publication> query = cb.createQuery(Publication.class);
            Root<Publication> root = query.from(Publication.class);

            query.select(root).where(cb.equal(root.get("client"), user));
            //query.orderBy(cb.desc(root.get("requestDate")));

            Query q = entityManager.createQuery(query);
            publications = q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return publications;
    }

    public List<Publication> filterBooks(User user, BookFilter bookFilter) {
        List<Publication> filteredPublications = new ArrayList<>();

        try {
            entityManager = entityManagerFactory.createEntityManager();
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Publication> query = cb.createQuery(Publication.class);
            Root<Publication> root = query.from(Publication.class);

            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("owner"), user));

            if (bookFilter.getPublicationType() != null) {
                Class<? extends Publication> publicationClass;

                switch (bookFilter.getPublicationType()) {
                    case Book -> publicationClass = Book.class;
                    case Periodical -> publicationClass = Periodical.class;
                    case Manga -> publicationClass = Manga.class;
                    default -> throw new IllegalArgumentException("Unknown publication type: " + bookFilter.getPublicationType());
                }

                predicates.add(cb.equal(root.type(), entityManager.getMetamodel().entity(publicationClass).getJavaType()));
            }

            if (bookFilter.getStatus() != null) {
                predicates.add(cb.equal(root.get("publicationStatus"), bookFilter.getStatus()));
            }

            if (bookFilter.getAuthor() != null && !bookFilter.getAuthor().isEmpty()) {
                predicates.add(cb.equal(root.get("author"), bookFilter.getAuthor()));
            }

            if (bookFilter.getTitle() != null && !bookFilter.getTitle().isEmpty()) {
                predicates.add(cb.equal(root.get("title"), bookFilter.getTitle()));
            }

            if (bookFilter.getPublicationDate() != null) {
                predicates.add(cb.equal(root.get("publicationDate"), bookFilter.getPublicationDate()));
            }

            query.select(root).where(cb.and(predicates.toArray(new Predicate[0])));

            Query q = entityManager.createQuery(query);
            filteredPublications = q.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filteredPublications;
    }
}
