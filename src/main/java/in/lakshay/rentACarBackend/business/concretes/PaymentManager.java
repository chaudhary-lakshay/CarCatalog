package in.lakshay.rentACarBackend.business.concretes;

// models for ordered extras
import in.lakshay.rentACarBackend.api.models.orderedAdditional.OrderedAdditionalAddModel;
import in.lakshay.rentACarBackend.api.models.orderedAdditional.OrderedAdditionalUpdateModel;

// rental car stuff
import in.lakshay.rentACarBackend.api.models.rentalCar.*;

// service interfaces - lots of em
import in.lakshay.rentACarBackend.business.abstracts.*;
import in.lakshay.rentACarBackend.business.constants.messaaages.BusinessMessages;

// dtos for data transfer
import in.lakshay.rentACarBackend.business.dtos.paymentDtos.gets.GetPaymentDto;
import in.lakshay.rentACarBackend.business.dtos.paymentDtos.lists.PaymentListDto;

// request models - omg so many! gotta refactor this mess someday
import in.lakshay.rentACarBackend.business.requests.orderedAdditionalRequests.CreateOrderedAdditionalRequest;
import in.lakshay.rentACarBackend.business.requests.paymentRequests.CreatePaymentRequest;
import in.lakshay.rentACarBackend.business.requests.rentalCarRequests.CreateRentalCarRequest;
import in.lakshay.rentACarBackend.business.requests.rentalCarRequests.UpdateDeliveryDateRequest;
import in.lakshay.rentACarBackend.business.requests.orderedAdditionalRequests.UpdateOrderedAdditionalRequest;
import in.lakshay.rentACarBackend.business.requests.rentalCarRequests.UpdateRentalCarRequest;

// payment gateway stuff
import in.lakshay.rentACarBackend.business.adapters.posAdapters.PosService;

// exceptions - way too many! srsly need to clean this up
import in.lakshay.rentACarBackend.core.utilities.exceptions.businessExceptions.BusinessException;
import in.lakshay.rentACarBackend.core.utilities.exceptions.businessExceptions.PosServiceExceptions.MakePaymentFailedException;
import in.lakshay.rentACarBackend.core.utilities.exceptions.businessExceptions.additionalExceptions.AdditionalNotFoundException;
import in.lakshay.rentACarBackend.core.utilities.exceptions.businessExceptions.carExceptions.CarNotFoundException;
import in.lakshay.rentACarBackend.core.utilities.exceptions.businessExceptions.carMaintenanceExceptions.CarAlreadyInMaintenanceException;
import in.lakshay.rentACarBackend.core.utilities.exceptions.businessExceptions.cityExceptions.CityNotFoundException;
import in.lakshay.rentACarBackend.core.utilities.exceptions.businessExceptions.corporateCustomerExceptions.CorporateCustomerNotFoundException;
import in.lakshay.rentACarBackend.core.utilities.exceptions.businessExceptions.customerExceptions.CustomerNotFoundException;
import in.lakshay.rentACarBackend.core.utilities.exceptions.businessExceptions.individualCustomerExceptions.IndividualCustomerNotFoundException;
import in.lakshay.rentACarBackend.core.utilities.exceptions.businessExceptions.orderedAdditionalExceptions.AdditionalQuantityNotValidException;
import in.lakshay.rentACarBackend.core.utilities.exceptions.businessExceptions.orderedAdditionalExceptions.OrderedAdditionalAlreadyExistsException;
import in.lakshay.rentACarBackend.core.utilities.exceptions.businessExceptions.orderedAdditionalExceptions.OrderedAdditionalNotFoundException;
import in.lakshay.rentACarBackend.core.utilities.exceptions.businessExceptions.paymentExceptions.PaymentNotFoundException;
import in.lakshay.rentACarBackend.core.utilities.exceptions.businessExceptions.paymentExceptions.RentalCarAlreadyExistsInPaymentException;
import in.lakshay.rentACarBackend.core.utilities.exceptions.businessExceptions.rentalCarExceptions.*;

// utils n stuff
import in.lakshay.rentACarBackend.core.utilities.mapping.ModelMapperService;
import in.lakshay.rentACarBackend.core.utilities.result.DataResult;
import in.lakshay.rentACarBackend.core.utilities.result.Result;
import in.lakshay.rentACarBackend.core.utilities.result.SuccessDataResult;
import in.lakshay.rentACarBackend.core.utilities.result.SuccessResult;

// data access layer
import in.lakshay.rentACarBackend.dataAccess.abstracts.PaymentDao;
import in.lakshay.rentACarBackend.entities.concretes.OrderedAdditional;
import in.lakshay.rentACarBackend.entities.concretes.Payment;
import in.lakshay.rentACarBackend.entities.concretes.RentalCar;

// spring magic
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// java stuff
import java.util.List;
import java.util.stream.Collectors;

@Service // handles payment business logic
public class PaymentManager implements PaymentService {
    // this is a big class that handles all payment operations
    // might need to split into smaller services someday

    private final PaymentDao paymentDao; // data access
    private final ModelMapperService modelMapperService; // for mapping
    private final CarService carService; // car operations
    private final RentalCarService rentalCarService; // rental operations
    private final OrderedAdditionalService orderedAdditionalService; // extras
    private final InvoiceService invoiceService; // invoice generation
    private final PosService posService; // payment gateway
    private final CreditCardService creditCardService; // credit card ops

    // wow that's a lot of dependencies! maybe too many?
    public PaymentManager(PaymentDao paymentDao, ModelMapperService modelMapperService,
                          @Lazy CarService carService, @Lazy RentalCarService rentalCarService,
                          @Lazy OrderedAdditionalService orderedAdditionalService,
                          @Lazy InvoiceService invoiceService, @Lazy PosService posService,
                          CreditCardService creditCardService) {
        // using @Lazy to avoid circular dependencies
        this.paymentDao = paymentDao;
        this.carService = carService;
        this.modelMapperService = modelMapperService;
        this.rentalCarService = rentalCarService;
        this.orderedAdditionalService = orderedAdditionalService;
        this.invoiceService = invoiceService;
        this.posService = posService; // payment gateway service
        this.creditCardService = creditCardService;
    }


    // get all payments from db
    @Override
    public DataResult<List<PaymentListDto>> getAll() {
        // grab everything from db
        List<Payment> payments = this.paymentDao.findAll();

        // map to dtos using streams... java 8 ftw i guess
        List<PaymentListDto> result = payments.stream()
                .map(payment -> this.modelMapperService.forDto()
                .map(payment, PaymentListDto.class))
                .collect(Collectors.toList());

        // gotta manually set ids cuz mapper is being weird
        manuelIdSetter(payments, result); // typo lol but too lazy to fix everywhere

        return new SuccessDataResult<>(result, BusinessMessages.GlobalMessages.DATA_LISTED_SUCCESSFULLY);
    }

    // process payment for individual customer rental
    @Override
    @Transactional(rollbackFor = BusinessException.class) // rollback if anything fails
    public Result makePaymentForIndividualRentAdd(MakePaymentForIndividualRentAdd makePayment, CreditCardManager.CardSaveInformation cardSaveInformation) throws AdditionalQuantityNotValidException, AdditionalNotFoundException, MakePaymentFailedException, CustomerNotFoundException, StartDateBeforeTodayException, StartDateBeforeFinishDateException, CarAlreadyRentedEnteredDateException, CarNotFoundException, IndividualCustomerNotFoundException, CarAlreadyInMaintenanceException, CityNotFoundException, RentalCarNotFoundException {

        // check everything first
        checkAllValidationsForIndividualAdd(makePayment.getCreateRentalCarRequest(), makePayment.getCreateOrderedAdditionalRequestList());

        // calc the price
        double totalPrice = calculateTotalPrice(makePayment.getCreateRentalCarRequest(), makePayment.getCreateOrderedAdditionalRequestList());

        // send to payment gateway
        this.posService.payment(makePayment.getCreateCreditCardRequest().getCardNumber(),
                               makePayment.getCreateCreditCardRequest().getCardOwner(),
                               makePayment.getCreateCreditCardRequest().getCardCvv(),
                               makePayment.getCreateCreditCardRequest().getCardExpirationDate(),
                               totalPrice);


        // do all the db stuff
        runPaymentSuccessorForIndividualRentAdd(makePayment, totalPrice, cardSaveInformation);

        return new SuccessResult(BusinessMessages.PaymentMessages.PAYMENT_AND_RENT_CAR_SUCCESSFULLY);
    }

    @Override
    @Transactional(rollbackFor = BusinessException.class)
    public Result makePaymentForCorporateRentAdd(MakePaymentForCorporateRentAdd makePayment, CreditCardManager.CardSaveInformation cardSaveInformation) throws CustomerNotFoundException, MakePaymentFailedException, AdditionalNotFoundException, AdditionalQuantityNotValidException, CorporateCustomerNotFoundException, StartDateBeforeTodayException, StartDateBeforeFinishDateException, CarAlreadyRentedEnteredDateException, CarNotFoundException, CarAlreadyInMaintenanceException, CityNotFoundException, RentalCarNotFoundException {

        checkAllValidationsForCorporateAdd(makePayment.getCreateRentalCarRequest(), makePayment.getCreateOrderedAdditionalRequestList());

        double totalPrice = calculateTotalPrice(makePayment.getCreateRentalCarRequest(), makePayment.getCreateOrderedAdditionalRequestList());

        this.posService.payment(makePayment.getCreateCreditCardRequest().getCardNumber(), makePayment.getCreateCreditCardRequest().getCardOwner(),
                makePayment.getCreateCreditCardRequest().getCardCvv(), makePayment.getCreateCreditCardRequest().getCardExpirationDate(), totalPrice);

        runPaymentSuccessorForCorporateRentAdd(makePayment, totalPrice, cardSaveInformation);

        return new SuccessResult(BusinessMessages.PaymentMessages.PAYMENT_AND_RENT_CAR_SUCCESSFULLY);
    }

    @Override
    @Transactional(rollbackFor = BusinessException.class)
    public Result makePaymentForIndividualRentUpdate(MakePaymentForIndividualRentUpdate makePaymentForIndividualRentUpdate, CreditCardManager.CardSaveInformation cardSaveInformation) throws CustomerNotFoundException, MakePaymentFailedException, RentalCarNotFoundException, StartDateBeforeTodayException, StartDateBeforeFinishDateException, CarAlreadyRentedEnteredDateException, CarNotFoundException, IndividualCustomerNotFoundException, CarAlreadyInMaintenanceException, CityNotFoundException, AdditionalNotFoundException {

        checkAllValidationsForIndividualUpdate(makePaymentForIndividualRentUpdate.getUpdateRentalCarRequest());

        double totalPrice = calculatePriceDifferenceWithPreviousRentalCar(makePaymentForIndividualRentUpdate.getUpdateRentalCarRequest());

        if(totalPrice > 0){
            this.posService.payment(makePaymentForIndividualRentUpdate.getCreateCreditCardRequest().getCardNumber(), makePaymentForIndividualRentUpdate.getCreateCreditCardRequest().getCardOwner(),
                    makePaymentForIndividualRentUpdate.getCreateCreditCardRequest().getCardCvv(), makePaymentForIndividualRentUpdate.getCreateCreditCardRequest().getCardExpirationDate(), totalPrice);

                runPaymentSuccessorForIndividualRentUpdate(makePaymentForIndividualRentUpdate, totalPrice, cardSaveInformation);

            return new SuccessResult(BusinessMessages.PaymentMessages.PAYMENT_AND_RENT_CAR_UPDATE_SUCCESSFULLY);
        }

        this.rentalCarService.updateForIndividualCustomer(makePaymentForIndividualRentUpdate.getUpdateRentalCarRequest());

        return new SuccessResult(BusinessMessages.PaymentMessages.PAYMENT_AND_RENT_CAR_UPDATE_SUCCESSFULLY);
    }

    @Override
    @Transactional(rollbackFor = BusinessException.class)
    public Result makePaymentForCorporateRentUpdate(MakePaymentForCorporateRentUpdate makePaymentForCorporateRentUpdate, CreditCardManager.CardSaveInformation cardSaveInformation) throws CustomerNotFoundException, MakePaymentFailedException, RentalCarNotFoundException, CorporateCustomerNotFoundException, StartDateBeforeTodayException, StartDateBeforeFinishDateException, CarAlreadyRentedEnteredDateException, CarNotFoundException, CarAlreadyInMaintenanceException, CityNotFoundException, AdditionalNotFoundException {

        checkAllValidationsForCorporateUpdate(makePaymentForCorporateRentUpdate.getUpdateRentalCarRequest());

        double totalPrice = calculatePriceDifferenceWithPreviousRentalCar(makePaymentForCorporateRentUpdate.getUpdateRentalCarRequest());

        if(totalPrice > 0){
            this.posService.payment(makePaymentForCorporateRentUpdate.getCreateCreditCardRequest().getCardNumber(), makePaymentForCorporateRentUpdate.getCreateCreditCardRequest().getCardOwner(),
                    makePaymentForCorporateRentUpdate.getCreateCreditCardRequest().getCardCvv(), makePaymentForCorporateRentUpdate.getCreateCreditCardRequest().getCardExpirationDate(), totalPrice);

            runPaymentSuccessorForCorporateRentUpdate(makePaymentForCorporateRentUpdate, totalPrice, cardSaveInformation);

            return new SuccessResult(BusinessMessages.PaymentMessages.PAYMENT_AND_RENT_CAR_UPDATE_SUCCESSFULLY);
        }

        this.rentalCarService.updateForCorporateCustomer(makePaymentForCorporateRentUpdate.getUpdateRentalCarRequest());

        return new SuccessResult(BusinessMessages.PaymentMessages.PAYMENT_AND_RENT_CAR_UPDATE_SUCCESSFULLY);
    }

    @Override
    @Transactional(rollbackFor = BusinessException.class)
    public Result makePaymentForRentDeliveryDateUpdate(MakePaymentForRentDeliveryDateUpdate makePaymentModel, CreditCardManager.CardSaveInformation cardSaveInformation) throws CustomerNotFoundException, MakePaymentFailedException, AdditionalNotFoundException, RentalCarNotFoundException, StartDateBeforeFinishDateException, DeliveredKilometerAlreadyExistsException, CarAlreadyRentedEnteredDateException, RentedKilometerNullException {

        checkAllValidationsForRentDeliveryDateUpdate(makePaymentModel.getUpdateDeliveryDateRequest());

        double totalPrice = calculateLateDeliveryPrice(makePaymentModel.getUpdateDeliveryDateRequest());

        this.posService.payment(makePaymentModel.getCreateCreditCardRequest().getCardNumber(), makePaymentModel.getCreateCreditCardRequest().getCardOwner(),
                makePaymentModel.getCreateCreditCardRequest().getCardCvv(), makePaymentModel.getCreateCreditCardRequest().getCardExpirationDate(), totalPrice);


        runPaymentSuccessorForRentDeliveryDateUpdate(makePaymentModel, totalPrice, cardSaveInformation);

        return new SuccessResult(BusinessMessages.PaymentMessages.PAYMENT_AND_RENT_DELIVERY_DATE_UPDATE_SUCCESSFULLY);
    }

    @Override
    @Transactional(rollbackFor = BusinessException.class)
    public Result makePaymentForOrderedAdditionalAdd(OrderedAdditionalAddModel orderedAdditionalAddModel, CreditCardManager.CardSaveInformation cardSaveInformation) throws CustomerNotFoundException, MakePaymentFailedException, AdditionalNotFoundException, AdditionalQuantityNotValidException, RentalCarNotFoundException {

        checkAllValidationsForOrderedAdditionalAdd(orderedAdditionalAddModel.getRentalCarId(), orderedAdditionalAddModel.getCreateOrderedAdditionalRequestList());

        double totalPrice = this.orderedAdditionalService.getPriceCalculatorForOrderedAdditionalList(orderedAdditionalAddModel.getCreateOrderedAdditionalRequestList(),
                                                                                    this.rentalCarService.getTotalDaysForRental(orderedAdditionalAddModel.getRentalCarId()));

        this.posService.payment(orderedAdditionalAddModel.getCreateCreditCardRequest().getCardNumber(), orderedAdditionalAddModel.getCreateCreditCardRequest().getCardOwner(),
                orderedAdditionalAddModel.getCreateCreditCardRequest().getCardCvv(), orderedAdditionalAddModel.getCreateCreditCardRequest().getCardExpirationDate(), totalPrice);

        runPaymentSuccessorForOrderedAdditionalAdd(orderedAdditionalAddModel, totalPrice, cardSaveInformation);

        return new SuccessResult(BusinessMessages.PaymentMessages.PAYMENT_AND_ADDITIONAL_SERVICE_ADDING_SUCCESSFULLY);
    }

    @Override
    @Transactional(rollbackFor = BusinessException.class)
    public Result makePaymentForOrderedAdditionalUpdate(OrderedAdditionalUpdateModel orderedAdditionalUpdateModel, CreditCardManager.CardSaveInformation cardSaveInformation) throws OrderedAdditionalNotFoundException, CustomerNotFoundException, MakePaymentFailedException, AdditionalNotFoundException, AdditionalQuantityNotValidException, OrderedAdditionalAlreadyExistsException, RentalCarNotFoundException {

        checkAllValidationsForOrderedAdditionalUpdate(orderedAdditionalUpdateModel.getUpdateOrderedAdditionalRequest());

        double totalPrice = calculatePriceDifferenceWithPreviousOrderedAdditional(orderedAdditionalUpdateModel.getUpdateOrderedAdditionalRequest());

        if(totalPrice > 0){
            this.posService.payment(orderedAdditionalUpdateModel.getCreateCreditCardRequest().getCardNumber(), orderedAdditionalUpdateModel.getCreateCreditCardRequest().getCardOwner(),
                    orderedAdditionalUpdateModel.getCreateCreditCardRequest().getCardCvv(), orderedAdditionalUpdateModel.getCreateCreditCardRequest().getCardExpirationDate(), totalPrice);

            runPaymentSuccessorForOrderedAdditionalUpdate(orderedAdditionalUpdateModel, totalPrice, cardSaveInformation);

            return new SuccessResult(BusinessMessages.PaymentMessages.PAYMENT_AND_ADDITIONAL_SERVICE_UPDATE_SUCCESSFULLY);
        }

        this.orderedAdditionalService.update(orderedAdditionalUpdateModel.getUpdateOrderedAdditionalRequest());

        return new SuccessResult(BusinessMessages.PaymentMessages.PAYMENT_AND_ADDITIONAL_SERVICE_UPDATE_SUCCESSFULLY);
    }

    @Transactional(rollbackFor = BusinessException.class)
    void runPaymentSuccessorForIndividualRentAdd(MakePaymentForIndividualRentAdd makePayment, double totalPrice, CreditCardManager.CardSaveInformation cardSaveInformation) throws CustomerNotFoundException, AdditionalNotFoundException, RentalCarNotFoundException {
        int rentalCarId = this.rentalCarService.addForIndividualCustomer(makePayment.getCreateRentalCarRequest());

        // create payment req
        CreatePaymentRequest createPaymentRequest = new CreatePaymentRequest();
        createPaymentRequest.setRentalCarId(rentalCarId);
        createPaymentRequest.setTotalPrice(totalPrice);
        makePayment.getCreateCreditCardRequest().setCustomerId(makePayment.getCreateRentalCarRequest().getCustomerId());

        // map and save
        Payment payment = this.modelMapperService.forRequest().map(createPaymentRequest, Payment.class);
        payment.setPaymentId(0);  // make sure it's new

        int paymentId = this.paymentDao.save(payment).getPaymentId();
        this.creditCardService.checkSaveInformationAndSaveCreditCard(makePayment.getCreateCreditCardRequest(), cardSaveInformation);
        this.orderedAdditionalService.saveOrderedAdditionalList(makePayment.getCreateOrderedAdditionalRequestList(), rentalCarId);
        this.invoiceService.createAndAddInvoice(rentalCarId, paymentId);
    }

    @Transactional(rollbackFor = BusinessException.class)
    void runPaymentSuccessorForCorporateRentAdd(MakePaymentForCorporateRentAdd makePayment, double totalPrice, CreditCardManager.CardSaveInformation cardSaveInformation) throws CustomerNotFoundException, AdditionalNotFoundException, RentalCarNotFoundException {

        int rentalCarId = this.rentalCarService.addForCorporateCustomer(makePayment.getCreateRentalCarRequest());

        CreatePaymentRequest createPaymentRequest = new CreatePaymentRequest();
        createPaymentRequest.setTotalPrice(totalPrice);
        createPaymentRequest.setRentalCarId(rentalCarId);
        makePayment.getCreateCreditCardRequest().setCustomerId(makePayment.getCreateRentalCarRequest().getCustomerId());

        Payment payment = this.modelMapperService.forRequest().map(createPaymentRequest, Payment.class);
        payment.setPaymentId(0);

        int paymentId = this.paymentDao.save(payment).getPaymentId();
        this.creditCardService.checkSaveInformationAndSaveCreditCard(makePayment.getCreateCreditCardRequest(), cardSaveInformation);
        this.orderedAdditionalService.saveOrderedAdditionalList(makePayment.getCreateOrderedAdditionalRequestList(), rentalCarId);
        this.invoiceService.createAndAddInvoice(rentalCarId, paymentId);
    }

    @Transactional(rollbackFor = BusinessException.class)
    void runPaymentSuccessorForIndividualRentUpdate(MakePaymentForIndividualRentUpdate makePayment, double totalPrice, CreditCardManager.CardSaveInformation cardSaveInformation) throws CustomerNotFoundException, RentalCarNotFoundException, AdditionalNotFoundException {

        CreatePaymentRequest createPaymentRequest = new CreatePaymentRequest();
        createPaymentRequest.setRentalCarId(makePayment.getUpdateRentalCarRequest().getRentalCarId());
        createPaymentRequest.setTotalPrice(totalPrice);
        makePayment.getCreateCreditCardRequest().setCustomerId(makePayment.getUpdateRentalCarRequest().getCustomerId());
        Payment payment = this.modelMapperService.forRequest().map(createPaymentRequest, Payment.class);
        payment.setPaymentId(0);
        int paymentId = this.paymentDao.save(payment).getPaymentId();
        this.rentalCarService.updateForIndividualCustomer(makePayment.getUpdateRentalCarRequest());
        this.creditCardService.checkSaveInformationAndSaveCreditCard(makePayment.getCreateCreditCardRequest(), cardSaveInformation);
        this.invoiceService.createAndAddInvoice(makePayment.getUpdateRentalCarRequest().getRentalCarId(), paymentId);
    }

    @Transactional(rollbackFor = BusinessException.class)
    void runPaymentSuccessorForCorporateRentUpdate(MakePaymentForCorporateRentUpdate makePayment, double totalPrice, CreditCardManager.CardSaveInformation cardSaveInformation) throws CustomerNotFoundException, RentalCarNotFoundException, AdditionalNotFoundException {

        CreatePaymentRequest createPaymentRequest = new CreatePaymentRequest();
        createPaymentRequest.setRentalCarId(makePayment.getUpdateRentalCarRequest().getRentalCarId());
        createPaymentRequest.setTotalPrice(totalPrice);
        makePayment.getCreateCreditCardRequest().setCustomerId(makePayment.getUpdateRentalCarRequest().getCustomerId());

        Payment payment = this.modelMapperService.forRequest().map(createPaymentRequest, Payment.class);
        payment.setPaymentId(0);


        int paymentId = this.paymentDao.save(payment).getPaymentId();
        this.rentalCarService.updateForCorporateCustomer(makePayment.getUpdateRentalCarRequest());
        this.creditCardService.checkSaveInformationAndSaveCreditCard(makePayment.getCreateCreditCardRequest(), cardSaveInformation);
        this.invoiceService.createAndAddInvoice(makePayment.getUpdateRentalCarRequest().getRentalCarId(), paymentId);
    }

    @Transactional(rollbackFor = BusinessException.class)
    void runPaymentSuccessorForRentDeliveryDateUpdate(MakePaymentForRentDeliveryDateUpdate makePaymentModel, double totalPrice, CreditCardManager.CardSaveInformation cardSaveInformation) throws CustomerNotFoundException, RentalCarNotFoundException, AdditionalNotFoundException {

        RentalCar rentalCar = this.rentalCarService.getById(makePaymentModel.getUpdateDeliveryDateRequest().getRentalCarId());

        CreatePaymentRequest createPaymentRequest = new CreatePaymentRequest();
        createPaymentRequest.setRentalCarId(makePaymentModel.getUpdateDeliveryDateRequest().getRentalCarId());
        createPaymentRequest.setTotalPrice(totalPrice);
        makePaymentModel.getCreateCreditCardRequest().setCustomerId(rentalCar.getCustomer().getCustomerId());
        rentalCar.setFinishDate(makePaymentModel.getUpdateDeliveryDateRequest().getFinishDate());

        UpdateRentalCarRequest request = this.modelMapperService.forDto().map(rentalCar, UpdateRentalCarRequest.class);
        request.setCustomerId(rentalCar.getCustomer().getCustomerId());
        Payment payment = this.modelMapperService.forRequest().map(createPaymentRequest, Payment.class);
        payment.setPaymentId(0);

        int paymentId = this.paymentDao.save(payment).getPaymentId();
        this.rentalCarService.updateForIndividualCustomer(request);
        this.creditCardService.checkSaveInformationAndSaveCreditCard(makePaymentModel.getCreateCreditCardRequest(), cardSaveInformation);
        this.invoiceService.createAndAddInvoice(rentalCar.getRentalCarId(), paymentId);
    }

    @Transactional(rollbackFor = BusinessException.class)
    void runPaymentSuccessorForOrderedAdditionalAdd(OrderedAdditionalAddModel orderedAdditionalAddModel, double totalPrice, CreditCardManager.CardSaveInformation cardSaveInformation) throws CustomerNotFoundException, RentalCarNotFoundException, AdditionalNotFoundException {

        RentalCar rentalCar = this.rentalCarService.getById(orderedAdditionalAddModel.getRentalCarId());

        CreatePaymentRequest createPaymentRequest = new CreatePaymentRequest();
        createPaymentRequest.setRentalCarId(orderedAdditionalAddModel.getRentalCarId());
        createPaymentRequest.setTotalPrice(totalPrice);
        orderedAdditionalAddModel.getCreateCreditCardRequest().setCustomerId(rentalCar.getCustomer().getCustomerId());

        Payment payment = this.modelMapperService.forRequest().map(createPaymentRequest, Payment.class);
        payment.setPaymentId(0);

        int paymentId = this.paymentDao.save(payment).getPaymentId();
        this.creditCardService.checkSaveInformationAndSaveCreditCard(orderedAdditionalAddModel.getCreateCreditCardRequest(), cardSaveInformation);
        this.orderedAdditionalService.saveOrderedAdditionalList(orderedAdditionalAddModel.getCreateOrderedAdditionalRequestList(), orderedAdditionalAddModel.getRentalCarId());
        this.invoiceService.createAndAddInvoice(orderedAdditionalAddModel.getRentalCarId(), paymentId);
    }

    @Transactional(rollbackFor = BusinessException.class)
    void runPaymentSuccessorForOrderedAdditionalUpdate(OrderedAdditionalUpdateModel orderedAdditionalUpdateModel, double totalPrice, CreditCardManager.CardSaveInformation cardSaveInformation) throws OrderedAdditionalNotFoundException, CustomerNotFoundException, RentalCarNotFoundException, AdditionalNotFoundException {

        RentalCar rentalCar = this.rentalCarService.getById(orderedAdditionalUpdateModel.getUpdateOrderedAdditionalRequest().getRentalCarId());

        CreatePaymentRequest createPaymentRequest = new CreatePaymentRequest();
        createPaymentRequest.setRentalCarId(orderedAdditionalUpdateModel.getUpdateOrderedAdditionalRequest().getRentalCarId());
        createPaymentRequest.setTotalPrice(totalPrice);
        orderedAdditionalUpdateModel.getCreateCreditCardRequest().setCustomerId(rentalCar.getCustomer().getCustomerId());

        Payment payment = this.modelMapperService.forRequest().map(createPaymentRequest, Payment.class);
        payment.setPaymentId(0);

        int paymentId = this.paymentDao.save(payment).getPaymentId();
        this.creditCardService.checkSaveInformationAndSaveCreditCard(orderedAdditionalUpdateModel.getCreateCreditCardRequest(), cardSaveInformation);
        this.orderedAdditionalService.update(orderedAdditionalUpdateModel.getUpdateOrderedAdditionalRequest());
        this.invoiceService.createAndAddInvoice(orderedAdditionalUpdateModel.getUpdateOrderedAdditionalRequest().getRentalCarId(), paymentId);
    }

    @Override
    public DataResult<GetPaymentDto> getById(int paymentId) throws PaymentNotFoundException {

        checkIfExistsByPaymentId(paymentId);

        Payment payment = this.paymentDao.getById(paymentId);

        GetPaymentDto result = this.modelMapperService.forDto().map(payment, GetPaymentDto.class);
        result.setRentalCarId(payment.getRentalCar().getRentalCarId());
        result.setInvoiceId(payment.getPaymentId());

        return new SuccessDataResult<>(result, BusinessMessages.GlobalMessages.DATA_BROUGHT_SUCCESSFULLY + paymentId );
    }

    @Override
    public DataResult<List<PaymentListDto>> getAllPaymentByRentalCar_RentalCarId(int rentalCarId) throws RentalCarNotFoundException {

        this.rentalCarService.checkIsExistsByRentalCarId(rentalCarId);

        List<Payment> payments = this.paymentDao.getAllByRentalCar_RentalCarId(rentalCarId);

        List<PaymentListDto> result = payments.stream().map(payment -> this.modelMapperService.forDto()
                .map(payment, PaymentListDto.class)).collect(Collectors.toList());
        manuelIdSetter(payments, result);

        return new SuccessDataResult<>(result, BusinessMessages.PaymentMessages.PAYMENT_LISTED_BY_RENTAL_CAR_ID + rentalCarId);
    }

    private void checkAllValidationsForIndividualAdd(CreateRentalCarRequest createRentalCarRequest, List<CreateOrderedAdditionalRequest> createOrderedAdditionalRequestList) throws AdditionalQuantityNotValidException, AdditionalNotFoundException, StartDateBeforeTodayException, StartDateBeforeFinishDateException, CarAlreadyRentedEnteredDateException, CarNotFoundException, IndividualCustomerNotFoundException, CustomerNotFoundException, CarAlreadyInMaintenanceException, CityNotFoundException {
        this.rentalCarService.checkAllValidationsForIndividualRentAdd(createRentalCarRequest);
        this.orderedAdditionalService.checkAllValidationForAddOrderedAdditionalList(createOrderedAdditionalRequestList);
    }

    private void checkAllValidationsForCorporateAdd(CreateRentalCarRequest createRentalCarRequest, List<CreateOrderedAdditionalRequest> createOrderedAdditionalRequestList) throws AdditionalQuantityNotValidException, AdditionalNotFoundException, CorporateCustomerNotFoundException, StartDateBeforeTodayException, StartDateBeforeFinishDateException, CarAlreadyRentedEnteredDateException, CarNotFoundException, CustomerNotFoundException, CarAlreadyInMaintenanceException, CityNotFoundException {
        this.rentalCarService.checkAllValidationsForCorporateRentAdd(createRentalCarRequest);
        this.orderedAdditionalService.checkAllValidationForAddOrderedAdditionalList(createOrderedAdditionalRequestList);
    }

    private void checkAllValidationsForIndividualUpdate(UpdateRentalCarRequest updateRentalCarRequest) throws StartDateBeforeTodayException, StartDateBeforeFinishDateException, CarAlreadyRentedEnteredDateException, CarNotFoundException, IndividualCustomerNotFoundException, CustomerNotFoundException, RentalCarNotFoundException, CarAlreadyInMaintenanceException, CityNotFoundException {
        this.rentalCarService.checkAllValidationsForIndividualRentUpdate(updateRentalCarRequest);
    }

    private void checkAllValidationsForCorporateUpdate(UpdateRentalCarRequest updateRentalCarRequest) throws CorporateCustomerNotFoundException, StartDateBeforeTodayException, StartDateBeforeFinishDateException, CarAlreadyRentedEnteredDateException, CarNotFoundException, CustomerNotFoundException, RentalCarNotFoundException, CarAlreadyInMaintenanceException, CityNotFoundException {
        this.rentalCarService.checkAllValidationsForCorporateRentUpdate(updateRentalCarRequest);
    }

    private void checkAllValidationsForOrderedAdditionalAdd(int rentalCarId, List<CreateOrderedAdditionalRequest> createOrderedAdditionalRequestList) throws AdditionalQuantityNotValidException, AdditionalNotFoundException, RentalCarNotFoundException {
        this.rentalCarService.checkIsExistsByRentalCarId(rentalCarId);
        this.orderedAdditionalService.checkAllValidationForAddOrderedAdditionalList(createOrderedAdditionalRequestList);
    }

    private void checkAllValidationsForOrderedAdditionalUpdate(UpdateOrderedAdditionalRequest updateOrderedAdditionalRequest) throws OrderedAdditionalAlreadyExistsException, AdditionalQuantityNotValidException, AdditionalNotFoundException, OrderedAdditionalNotFoundException, RentalCarNotFoundException {
        this.orderedAdditionalService.checkIsExistsByOrderedAdditionalId(updateOrderedAdditionalRequest.getOrderedAdditionalId());
        this.rentalCarService.checkIsExistsByRentalCarId(updateOrderedAdditionalRequest.getRentalCarId());
        this.orderedAdditionalService.checkAllValidationForAddOrderedAdditional(updateOrderedAdditionalRequest.getAdditionalId(), updateOrderedAdditionalRequest.getOrderedAdditionalQuantity());
        this.orderedAdditionalService.checkIsOnlyOneOrderedAdditionalByAdditionalIdAndRentalCarIdForUpdate(updateOrderedAdditionalRequest.getAdditionalId(), updateOrderedAdditionalRequest.getRentalCarId());
    }

    private void checkAllValidationsForRentDeliveryDateUpdate(UpdateDeliveryDateRequest updateDeliveryDateRequest) throws DeliveredKilometerAlreadyExistsException, RentedKilometerNullException, CarAlreadyRentedEnteredDateException, StartDateBeforeFinishDateException, RentalCarNotFoundException {

        this.rentalCarService.checkIsExistsByRentalCarId(updateDeliveryDateRequest.getRentalCarId());
        RentalCar rentalCar = this.rentalCarService.getById(updateDeliveryDateRequest.getRentalCarId());

        this.rentalCarService.checkIfFirstDateBeforeSecondDate(rentalCar.getFinishDate(),updateDeliveryDateRequest.getFinishDate());
        this.rentalCarService.checkIfCarAlreadyRentedForDeliveryDateUpdate(rentalCar.getCar().getCarId(), updateDeliveryDateRequest.getFinishDate(), rentalCar.getRentalCarId());
        this.rentalCarService.checkIfRentedKilometerIsNotNull(rentalCar.getRentedKilometer());
        this.rentalCarService.checkIfDeliveredKilometerIsNull(rentalCar.getDeliveredKilometer());
    }


    private double calculateTotalPrice(CreateRentalCarRequest rentalCarRequest, List<CreateOrderedAdditionalRequest> orderedAdditionalRequestList) throws AdditionalNotFoundException {

        int totalDays = this.rentalCarService.getTotalDaysForRental(rentalCarRequest.getStartDate(), rentalCarRequest.getFinishDate());
        double priceOfRental = this.rentalCarService.calculateAndReturnRentPrice(rentalCarRequest.getStartDate(), rentalCarRequest.getFinishDate(),
                this.carService.getDailyPriceByCarId(rentalCarRequest.getCarId()), rentalCarRequest.getRentedCityCityId(), rentalCarRequest.getDeliveredCityId());
        double priceOfAdditionals = this.orderedAdditionalService.getPriceCalculatorForOrderedAdditionalList(orderedAdditionalRequestList,totalDays);

        return priceOfRental + priceOfAdditionals;
    }

    private double calculateLateDeliveryPrice(UpdateDeliveryDateRequest updateDeliveryDateRequest) throws AdditionalNotFoundException, RentalCarNotFoundException {

        RentalCar rentalCar = this.rentalCarService.getById(updateDeliveryDateRequest.getRentalCarId());
        int totalDays = this.rentalCarService.getTotalDaysForRental(rentalCar.getFinishDate(),updateDeliveryDateRequest.getFinishDate());

        double priceOfRental = this.rentalCarService.calculateRentalCarTotalDayPrice(rentalCar.getFinishDate(), updateDeliveryDateRequest.getFinishDate(),
                this.carService.getDailyPriceByCarId(rentalCar.getCar().getCarId()));
        double priceOfAdditionals = this.orderedAdditionalService.getPriceCalculatorForOrderedAdditionalListByRentalCarId(rentalCar.getRentalCarId(), totalDays);

        return priceOfRental + priceOfAdditionals;
    }

    private double calculatePriceDifferenceWithPreviousRentalCar(UpdateRentalCarRequest updateRentalCarRequest) throws RentalCarNotFoundException {

        RentalCar beforeRentalCar = this.rentalCarService.getById(updateRentalCarRequest.getRentalCarId());

        double previousPrice = this.rentalCarService.calculateAndReturnRentPrice(beforeRentalCar.getStartDate(), beforeRentalCar.getFinishDate(), beforeRentalCar.getCar().getDailyPrice(),
                                                                            beforeRentalCar.getRentedCity().getCityId(), beforeRentalCar.getDeliveredCity().getCityId());
        double nextPrice = this.rentalCarService.calculateAndReturnRentPrice(updateRentalCarRequest.getStartDate(), updateRentalCarRequest.getFinishDate(),
                this.carService.getDailyPriceByCarId(updateRentalCarRequest.getCarId()), updateRentalCarRequest.getRentedCityId(), updateRentalCarRequest.getDeliveredCityId());

        if(nextPrice>previousPrice){
            return nextPrice-previousPrice;
        }
        return 0;
    }

    private double calculatePriceDifferenceWithPreviousOrderedAdditional(UpdateOrderedAdditionalRequest updateOrderedAdditionalRequest) throws AdditionalNotFoundException {
        OrderedAdditional orderedAdditional = this.orderedAdditionalService.getById(updateOrderedAdditionalRequest.getOrderedAdditionalId());

        double previousPrice = this.orderedAdditionalService.getPriceCalculatorForOrderedAdditional(orderedAdditional.getAdditional().getAdditionalId(),
                orderedAdditional.getOrderedAdditionalQuantity(), this.rentalCarService.getTotalDaysForRental(orderedAdditional.getRentalCar().getRentalCarId()));

        double nextPrice = this.orderedAdditionalService.getPriceCalculatorForOrderedAdditional(updateOrderedAdditionalRequest.getAdditionalId(),
                updateOrderedAdditionalRequest.getOrderedAdditionalQuantity(), this.rentalCarService.getTotalDaysForRental(updateOrderedAdditionalRequest.getRentalCarId()));

        if(nextPrice>previousPrice){
            return nextPrice - previousPrice;
        }
        return 0;
    }

    @Override
    public void checkIfExistsByPaymentId(int paymentId) throws PaymentNotFoundException {
        if(!this.paymentDao.existsByPaymentId(paymentId)){
            throw new PaymentNotFoundException(BusinessMessages.PaymentMessages.PAYMENT_ID_NOT_FOUND + paymentId);
        }
    }
    @Override
    public void checkIfNotExistsRentalCar_RentalCarId(int rentalCarId) throws RentalCarAlreadyExistsInPaymentException {
        if(this.paymentDao.existsByRentalCar_RentalCarId(rentalCarId)){
            throw new RentalCarAlreadyExistsInPaymentException(BusinessMessages.PaymentMessages.RENTAL_CAR_ID_ALREADY_EXISTS_IN_THE_PAYMENT_TABLE  + rentalCarId);
        }
    }

    // hacky fix for mapping issue - should fix this properly someday
    private void manuelIdSetter(List<Payment> paymentList, List<PaymentListDto> paymentListDtoList){
        // just loop thru and set ids manually... not elegant but works
        for(int i=0;i<paymentList.size();i++){
            paymentListDtoList.get(i).setRentalCarId(paymentList.get(i).getRentalCar().getRentalCarId());
        }
        // TODO: fix the mapper config instead of this workaround
    }
}
