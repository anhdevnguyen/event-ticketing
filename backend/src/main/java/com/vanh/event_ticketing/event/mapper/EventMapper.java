// Package: com.vanh.event_ticketing.event.mapper
// File: EventMapper.java
//
// Vai trò: Mapper chuyển đổi giữa Event entity và EventResponse DTO.
// Annotate @Component hoặc MapStruct @Mapper
//
// === METHODS ===
//
// EventResponse toResponse(Event event)
//   - Map: event.getId() -> id
//   - Map: event.getName() -> name
//   - Map: event.getDescription() -> description
//   - Map: event.getBannerUrl() -> bannerUrl
//   - Map: event.getStartTime() -> startTime
//   - Map: event.getEndTime() -> endTime
//   - Map: event.getLocation() -> location
//   - Map: event.getStatus().name() -> status (String)
//   - Map: event.getOrganizer().getDisplayName() -> organizerName
//   - Map: event.getOrganizer().getId() -> organizerId
//   - Chú ý: getOrganizer() có thể trigger lazy load — đảm bảo gọi trong @Transactional
//
// List<EventResponse> toResponseList(List<Event> events)
//   - Gọi toResponse() cho mỗi phần tử
//   - Có thể dùng stream().map(this::toResponse).collect(...)
//
// === GHI CHÚ KỸ THUẬT ===
// - Nếu dùng MapStruct:
//   @Mapper(componentModel = "spring")
//   @Mapping(target = "organizerName", source = "organizer.displayName")
//   @Mapping(target = "organizerId", source = "organizer.id")
//   @Mapping(target = "status", expression = "java(event.getStatus().name())")
// - Cẩn thận LazyInitializationException — mapper phải được gọi trong transaction
