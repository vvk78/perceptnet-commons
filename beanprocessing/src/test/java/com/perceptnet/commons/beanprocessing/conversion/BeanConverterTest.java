package com.perceptnet.commons.beanprocessing.conversion;

import com.perceptnet.commons.beanprocessing.conversion.data.dos.AuthorDo;
import com.perceptnet.commons.beanprocessing.conversion.data.dos.BookDo;
import com.perceptnet.commons.beanprocessing.conversion.data.dtos.AuthorWithBooksDto;
import com.perceptnet.commons.beanprocessing.conversion.data.dtos.BookShortDto;
import com.perceptnet.commons.beanprocessing.conversion.data.dtos.LookupDto;
import com.perceptnet.commons.reflection.BeanReflectionProviderCachingImpl;
import com.perceptnet.commons.reflection.ReflectionProvider;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.*;
import static com.perceptnet.commons.tests.TestGroups.*;

/**
 * created by vkorovkin (vkorovkin@gmail.com) on 28.08.2017
 */
public class BeanConverterTest {
    private ReflectionProvider reflectionProvider = new BeanReflectionProviderCachingImpl();

    private ConversionContext ctx;
    private BeanConverter<?> converter;

    private Map<Long, AuthorDo> data = new HashMap<>();

    @BeforeClass(groups = {UNIT})
    private void beforeClass() {
        data = new HashMap<>();
        createTestData();
    }

    @BeforeMethod(groups={UNIT})
    public void beforeMethod() throws Exception {
        ctx = new ConversionContext(reflectionProvider, reflectionProvider);
        converter = new BeanConverter(ctx);
    }

    @Test(groups = {UNIT})
    public void testLookupConversion() throws Exception {
        AuthorDo akunin = data.get(1L);
        LookupDto dto = converter.process(akunin, new LookupDto());
        assertEquals(dto.getId(), akunin.getId(), "Id is not converted");
        assertEquals(dto.getName(), akunin.getName(), "Name is not converted");
    }

    @Test(groups = {UNIT})
    public void testMasterConversion() throws Exception {
        BookDo leviafan = data.get(1L).getBooks().get(0);
        BookShortDto dto = converter.process(leviafan, new BookShortDto());
        assertEquals(dto.getId(), leviafan .getId(), "Id is not converted");
        assertEquals(dto.getTitle(), leviafan.getTitle(), "Title is not converted");
        assertEquals(dto.getAuthor().getId(), leviafan.getAuthor().getId(), "Master id is not converted");
        assertEquals(dto.getAuthor().getName(), leviafan.getAuthor().getName(), "Master name is not converted");
    }

    @Test(groups = {UNIT})
    public void testChildrenWithBackMasterReference() throws Exception {
        AuthorDo akunin = data.get(1L);
        AuthorWithBooksDto dto = converter.process(akunin, new AuthorWithBooksDto());
        assertEquals(dto.getId(), akunin.getId(), "Id is not converted");
        assertEquals(dto.getName(), akunin.getName(), "Name is not converted");
        assertEquals(dto.getBooks().size(), akunin.getBooks().size(), "Books are no converted");

        BookShortDto bookDto = dto.getBooks().get(0);
        BookDo bookDo = akunin.getBooks().get(0);
        assertEquals(bookDto.getId(), bookDo.getId(), "Book id is not converted");
        assertEquals(bookDto.getTitle(), bookDo.getTitle(), "Book title is not converted");
        assertEquals(bookDto.getAuthor().getId(), bookDo.getAuthor().getId(), "Book.author.id is not converted");
        assertEquals(bookDto.getAuthor().getName(), bookDo.getAuthor().getName(), "Book.author.name is not converted");

        bookDto = dto.getBooks().get(1);
        bookDo = akunin.getBooks().get(1);
        assertEquals(bookDto.getId(), bookDo.getId(), "Book id is not converted");
        assertEquals(bookDto.getTitle(), bookDo.getTitle(), "Book title is not converted");
        assertEquals(bookDto.getAuthor().getId(), bookDo.getAuthor().getId(), "Book.author.id is not converted");
        assertEquals(bookDto.getAuthor().getName(), bookDo.getAuthor().getName(), "Book.author.name is not converted");

        assertTrue(dto.getBooks().get(0).getAuthor() == dto.getBooks().get(1).getAuthor(), "Book.author is not the same object in books 0 and 1");


    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    private void createTestData() {
        AuthorDo a;
        a = insertAuthor("Boris Akunin");
        insertBook(a, "Leviafan", 4);
        insertBook(a, "Water World", 2);
        insertBook(a, "Diamond Chariot", 5);

        a = insertAuthor("Viktor Pelevin");
        insertBook(a, "S.N.U.F", 5);
        insertBook(a, "T", 3);
        insertBook(a, "Empire V", 5);
        insertBook(a, "Chapaev", 4);

        a = insertAuthor("Lev Tolstoy");
        insertBook(a, "War and Peace", 3);
    }

    private AuthorDo insertAuthor(String name) {
        AuthorDo result = new AuthorDo();
        result.setId(data.size() + 1L);
        result.setName(name);
        data.put(result.getId(), result);
        return result;
    }

    private BookDo insertBook(AuthorDo author, String title, int userRating) {
        BookDo result = new BookDo();
        result.setId(author.getId() * 100 + author.getBooks().size());
        result.setAuthor(author);
        author.getBooks().add(result);
        result.setTitle(title);
        return result;
    }

}
