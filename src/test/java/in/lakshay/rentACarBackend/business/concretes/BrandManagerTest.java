package in.lakshay.rentACarBackend.business.concretes;

import in.lakshay.rentACarBackend.business.abstracts.CarService;
import in.lakshay.rentACarBackend.business.requests.brandRequests.CreateBrandRequest;
import in.lakshay.rentACarBackend.core.utilities.exceptions.businessExceptions.brandExceptions.BrandAlreadyExistsException;
import in.lakshay.rentACarBackend.core.utilities.mapping.ModelMapperManager;
import in.lakshay.rentACarBackend.core.utilities.result.Result;
import in.lakshay.rentACarBackend.dataAccess.abstracts.BrandDao;
import in.lakshay.rentACarBackend.entities.concretes.Brand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// plain Mockito unit tests for the brand business rules - no Spring context
class BrandManagerTest {

	private BrandDao brandDao;
	private BrandManager brandManager;

	@BeforeEach
	void setUp() {
		brandDao = mock(BrandDao.class);
		CarService carService = mock(CarService.class);
		brandManager = new BrandManager(brandDao, new ModelMapperManager(new ModelMapper()), carService);
	}

	@Test
	void add_savesBrand_whenNameIsUnique() throws BrandAlreadyExistsException {
		when(brandDao.existsByBrandName("BMW")).thenReturn(false);

		Result result = brandManager.add(new CreateBrandRequest("BMW"));

		assertTrue(result.isSuccess());
		verify(brandDao).save(any(Brand.class));
	}

	@Test
	void add_throwsAndDoesNotSave_whenNameAlreadyExists() {
		when(brandDao.existsByBrandName("BMW")).thenReturn(true);

		assertThrows(BrandAlreadyExistsException.class,
				() -> brandManager.add(new CreateBrandRequest("BMW")));
		verify(brandDao, never()).save(any(Brand.class));
	}

}
