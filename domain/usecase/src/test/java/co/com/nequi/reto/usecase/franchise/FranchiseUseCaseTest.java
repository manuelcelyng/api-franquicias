package co.com.nequi.reto.usecase.franchise;

import co.com.nequi.reto.model.franchise.Franchise;
import co.com.nequi.reto.model.franchise.gateways.FranchiseRepository;
import co.com.nequi.reto.usecase.exceptions.FranchiseAlreadyExists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FranchiseUseCaseTest {

    @Mock
    private FranchiseRepository franchiseRepository;

    @InjectMocks
    private FranchiseUseCase useCase;

    private Franchise input;

    @BeforeEach
    void setUp() {
        input = Franchise.builder().name("Test Franchise").build();
    }

    @Test
    void createFranchise_shouldSave_whenNameNotExists() {
        when(franchiseRepository.findByName("Test Franchise")).thenReturn(Mono.empty());
        when(franchiseRepository.save(any(Franchise.class)))
                .thenAnswer(invocation -> Mono.just(
                        Franchise.builder().id(1L).name(((Franchise) invocation.getArgument(0)).getName()).build()
                ));

        Mono<Franchise> result = useCase.createFranchise(input);

        StepVerifier.create(result)
                .expectNextMatches(f -> f.getId() != null && f.getName().equals("Test Franchise"))
                .verifyComplete();
    }

    @Test
    void createFranchise_shouldError_whenNameAlreadyExists() {
        when(franchiseRepository.findByName("Test Franchise"))
                .thenReturn(Mono.just(Franchise.builder().id(99L).name("Test Franchise").build()));

        Mono<Franchise> result = useCase.createFranchise(input);

        StepVerifier.create(result)
                .expectErrorSatisfies(err -> {
                    assert err instanceof FranchiseAlreadyExists;
                    FranchiseAlreadyExists ex = (FranchiseAlreadyExists) err;
                    assert ex.getCode().equals("FRN_002");
                })
                .verify();
    }
}
