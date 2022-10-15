package ru.practicum.server.request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.server.exceptions.ResourceNotFoundException;
import ru.practicum.server.exceptions.ValidationException;
import ru.practicum.server.item.dto.ItemDto;
import ru.practicum.server.item.dto.ItemMapper;
import ru.practicum.server.item.repositoriy.ItemRepository;
import ru.practicum.server.request.dto.ItemRequestDtoForRequest;
import ru.practicum.server.request.dto.ItemRequestMapper;
import ru.practicum.server.request.dto.ItemRequestWithUsersResponseDto;
import ru.practicum.server.request.model.ItemRequest;
import ru.practicum.server.request.repository.ItemRequestRepository;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemRequestServiceDBImpl implements ItemRequestService {

    private final UserService userService;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;

    public ItemRequestServiceDBImpl(UserService userService,
                                    ItemRequestRepository itemRequestRepository,
                                    ItemRepository itemRepository) {
        this.userService = userService;
        this.itemRequestRepository = itemRequestRepository;
        this.itemRepository = itemRepository;
    }

    public ItemRequestWithUsersResponseDto getItemRequestWithResponse(int requestId, int userId) {
        userService.getUser(userId);
        return getItemRequestWithUsersResponse(getItemRequest(requestId));
    }

    public ItemRequest getItemRequest(int requestId) {
        Optional<ItemRequest> itemRequest = itemRequestRepository.findById(requestId);
        if (itemRequest.isEmpty()) {
            log.warn("Запрос данных о несуществующем запросе вещи с id - {}", requestId);
            throw new ResourceNotFoundException(String.format("Запроса вещи с id = %s не существует", requestId));
        }
        return itemRequest.get();
    }

    public List<ItemRequestWithUsersResponseDto> getUserItemRequestList(int userId) {
        User user = userService.getUser(userId);
        List<ItemRequest> userItemRequestList = itemRequestRepository.getUserItemRequestList(user);
        return userItemRequestList.stream()
                .map(this::getItemRequestWithUsersResponse)
                .collect(Collectors.toList());
    }

    public List<ItemRequestWithUsersResponseDto> getPageableItemRequestList(
            int userId, int from, Optional<Integer> size) {
        User user = userService.getUser(userId);
        Integer requiredSize = size.orElse(null);
        if (requiredSize == null) {
            return Collections.emptyList();
        }
        if (requiredSize < 1) {
            throw new ValidationException("Минимальное количество запросов для отображения не может быть меньше 1");
        }
        List<ItemRequest> pageableItemRequestList = itemRequestRepository.getPageableItemRequestList(
                PageRequest.of(from / requiredSize, requiredSize), user);
        return pageableItemRequestList.stream()
                .map(this::getItemRequestWithUsersResponse)
                .collect(Collectors.toList());
    }

    public ItemRequest addItemRequest(ItemRequestDtoForRequest newItemRequest, int userId) {
        User requestor = userService.getUser(userId);
        return itemRequestRepository.save(ItemRequestMapper.fromItemRequestDtoForRequest(newItemRequest, requestor));
    }

    private ItemRequestWithUsersResponseDto getItemRequestWithUsersResponse(ItemRequest itemRequest) {
        List<ItemDto> suggestedItemsForRequest = itemRepository.getItemsFotItemRequest(itemRequest)
                .stream()
                .map((ItemMapper::toItemDto))
                .collect(Collectors.toList());
        return ItemRequestMapper.toItemRequestWithUsersResponseDto(itemRequest, suggestedItemsForRequest);
    }
}
